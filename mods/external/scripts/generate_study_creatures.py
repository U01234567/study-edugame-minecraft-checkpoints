from __future__ import annotations

import argparse
import json
import math
import re
import shutil
import sys
from pathlib import Path
from typing import Iterable

sys.path.append(str(Path(__file__).resolve().parent))

from study_creature_codegen import (
    AnimationClipDef,
    AnimationTrackDef,
    CreatureModelDef,
    CubeDef,
    PartDef,
    emit_model_java,
    snake_to_pascal,
)

CUSTOM_CARD_RE = re.compile(r'Map\.entry\("([^"]+)"\s*,\s*new\s+CreatureCard\(', re.MULTILINE)
TEXTURE_SIZE_RE = re.compile(r"TexturedModelData\.of\([^,]+,\s*(\d+)\s*,\s*(\d+)\s*\)", re.MULTILINE | re.DOTALL)
PART_STMT_RE = re.compile(
    r'ModelPartData\s+(\w+)\s*=\s*(\w+)\.addChild\("([^"]+)",\s*(ModelPartBuilder\.create\(\).*?),\s*(ModelTransform\.(?:pivot|of)\(.*?\))\s*\);',
    re.MULTILINE | re.DOTALL,
)


def extract_call_args(text: str, open_paren_index: int) -> tuple[str, int]:
    depth = 0
    for idx in range(open_paren_index, len(text)):
        ch = text[idx]
        if ch == "(":
            depth += 1
        elif ch == ")":
            depth -= 1
            if depth == 0:
                return text[open_paren_index + 1:idx], idx
    raise ValueError("unbalanced parentheses")


def parse_float_token(token: str) -> float:
    return float(token.replace("F", "").replace("f", "").strip())


def split_top_level_csv(text: str) -> list[str]:
    out: list[str] = []
    depth = 0
    current: list[str] = []
    for ch in text:
        if ch == "," and depth == 0:
            out.append("".join(current).strip())
            current = []
            continue
        if ch == "(":
            depth += 1
        elif ch == ")":
            depth -= 1
        current.append(ch)
    if current:
        out.append("".join(current).strip())
    return out


def parse_builder_cubes(builder: str) -> list[CubeDef]:
    cubes: list[CubeDef] = []
    idx = 0
    compact = re.sub(r"\s+", " ", builder)

    while True:
        uv_idx = compact.find(".uv(", idx)
        if uv_idx == -1:
            break
        uv_args, uv_end = extract_call_args(compact, uv_idx + len(".uv"))
        cuboid_idx = compact.find(".cuboid(", uv_end)
        if cuboid_idx == -1:
            break
        cuboid_args, cuboid_end = extract_call_args(compact, cuboid_idx + len(".cuboid"))

        uv_parts = [int(part.strip()) for part in uv_args.split(",")]
        raw_parts = split_top_level_csv(cuboid_args)

        numeric_parts: list[float] = []
        for part in raw_parts:
            if part.startswith("new Dilation("):
                break
            numeric_parts.append(parse_float_token(part))

        if len(numeric_parts) >= 6:
            cubes.append(
                CubeDef(
                    u=uv_parts[0],
                    v=uv_parts[1],
                    x=numeric_parts[0],
                    y=numeric_parts[1],
                    z=numeric_parts[2],
                    dx=numeric_parts[3],
                    dy=numeric_parts[4],
                    dz=numeric_parts[5],
                )
            )

        idx = cuboid_end + 1

    return cubes


def parse_transform(transform: str) -> tuple[tuple[float, float, float], tuple[float, float, float]]:
    compact = re.sub(r"\s+", " ", transform)
    if compact.startswith("ModelTransform.pivot("):
        args, _ = extract_call_args(compact, compact.index("("))
        parts = [parse_float_token(part) for part in split_top_level_csv(args)]
        return (parts[0], parts[1], parts[2]), (0.0, 0.0, 0.0)

    if compact.startswith("ModelTransform.of("):
        args, _ = extract_call_args(compact, compact.index("("))
        parts = [parse_float_token(part) for part in split_top_level_csv(args)]
        return (parts[0], parts[1], parts[2]), (parts[3], parts[4], parts[5])

    raise ValueError(f"Unsupported transform: {transform}")


def parse_blockbench_model(creature_id: str, source: str) -> CreatureModelDef:
    texture_match = TEXTURE_SIZE_RE.search(source)
    if not texture_match:
        raise ValueError(f"{creature_id}: could not find texture size in model.java")

    texture_w = int(texture_match.group(1))
    texture_h = int(texture_match.group(2))

    parts: list[PartDef] = []
    var_to_name: dict[str, str] = {"modelPartData": None}  # sentinel root of the mesh definition

    for match in PART_STMT_RE.finditer(source):
        var_name = match.group(1)
        parent_var = match.group(2)
        part_name = match.group(3)
        builder = match.group(4)
        transform = match.group(5)

        pivot, rotation = parse_transform(transform)
        cubes = parse_builder_cubes(builder)

        parent_name = var_to_name.get(parent_var, parent_var)
        parts.append(
            PartDef(
                name=part_name,
                parent=parent_name,
                pivot=pivot,
                rotation=rotation,
                cubes=cubes,
            )
        )
        var_to_name[var_name] = part_name

    if not any(part.name == "root" for part in parts):
        root_part = PartDef(name="root", parent=None, pivot=(0.0, 24.0, 0.0), rotation=(0.0, 0.0, 0.0), cubes=[])
        parts.insert(0, root_part)
        for part in parts[1:]:
            if part.parent is None:
                part.parent = "root"

    notes = []
    if "model-anim.java" in source:
        notes.append("Blockbench animation sidecar exists but is not auto-merged by this parser.")

    return CreatureModelDef(
        creature_id=creature_id,
        class_name=f"{snake_to_pascal(creature_id)}Model",
        texture_width=texture_w,
        texture_height=texture_h,
        parts=parts,
        animation_notes=notes,
    )


def choose_named_clip(clips: list[AnimationClipDef], keywords: tuple[str, ...]) -> str | None:
    for clip in clips:
        lowered = clip.name.lower()
        if any(keyword in lowered for keyword in keywords):
            return clip.name
    return None


def parse_blockbench_animation_sidecar(source: str) -> list[AnimationClipDef]:
    """
    Supports both:
      - newer Blockbench exports: model-anim.java with AnimationDefinition.Builder.withLength(...)
      - older Blockbench exports: *-anim.txt with Animation.Builder.create(...)
    """
    header_re = re.compile(
        r"public\s+static\s+final\s+"
        r"(?P<kind>AnimationDefinition|Animation)\s+"
        r"(?P<name>\w+)\s*=\s*"
        r"(?P<builder>AnimationDefinition\.Builder\.withLength|Animation\.Builder\.create)"
        r"\((?P<length_args>[^)]*)\)",
        re.MULTILINE,
    )
    
    matches = list(header_re.finditer(source))
    if not matches:
        return []
    
    clips: list[AnimationClipDef] = []
    
    for clip_index, clip_match in enumerate(matches):
        block_start = clip_match.start()
        block_end = matches[clip_index + 1].start() if clip_index + 1 < len(matches) else len(source)
        block = source[block_start:block_end]
        
        build_end = block.find(".build();")
        if build_end != -1:
            block = block[: build_end + len(".build();")]
            
        clip_name = clip_match.group("name")
        clip_length_args = split_top_level_csv(clip_match.group("length_args"))
        clip_length = parse_float_token(clip_length_args[0])
        clip_kind = clip_match.group("kind")
        
        tracks_by_part: dict[str, AnimationTrackDef] = {}
        
        if clip_kind == "AnimationDefinition":
            track_re = re.compile(
                r'\.addAnimation\("([^"]+)",\s*new\s+AnimationChannel\(AnimationChannel\.Targets\.(POSITION|ROTATION|SCALE),',
                re.MULTILINE,
            )
            keyframe_patterns = {
                "POSITION": [
                    re.compile(r"new\s+Keyframe\(([^,]+),\s*KeyframeAnimations\.posVec\(([^)]*)\)")
                ],
                "ROTATION": [
                    re.compile(r"new\s+Keyframe\(([^,]+),\s*KeyframeAnimations\.degreeVec\(([^)]*)\)")
                ],
                "SCALE": [
                    re.compile(r"new\s+Keyframe\(([^,]+),\s*KeyframeAnimations\.scaleVec\(([^)]*)\)")
                ],
            }
        else:
            track_re = re.compile(
                r'\.addBoneAnimation\("([^"]+)",\s*new\s+Transformation\(Transformation\.Targets\.(TRANSLATE|ROTATE|SCALE),',
                re.MULTILINE,
            )
            keyframe_patterns = {
                "TRANSLATE": [
                    re.compile(r"new\s+Keyframe\(([^,]+),\s*AnimationHelper\.createTranslationalVector\(([^)]*)\)")
                ],
                "ROTATE": [
                    re.compile(r"new\s+Keyframe\(([^,]+),\s*AnimationHelper\.createRotationalVector\(([^)]*)\)")
                ],
                "SCALE": [
                    re.compile(r"new\s+Keyframe\(([^,]+),\s*AnimationHelper\.createScalingVector\(([^)]*)\)"),
                    re.compile(r"new\s+Keyframe\(([^,]+),\s*AnimationHelper\.createScaleVector\(([^)]*)\)"),
                ],
            }
            
        track_matches = list(track_re.finditer(block))
        
        for track_index, track_match in enumerate(track_matches):
            track_body_start = track_match.end()
            track_body_end = track_matches[track_index + 1].start() if track_index + 1 < len(track_matches) else len(block)
            track_body = block[track_body_start:track_body_end]
            
            part_name = track_match.group(1)
            target = track_match.group(2)
            track = tracks_by_part.setdefault(part_name, AnimationTrackDef(part_name=part_name))
            
            keyframe_matches = []
            for pattern in keyframe_patterns[target]:
                keyframe_matches = list(pattern.finditer(track_body))
                if keyframe_matches:
                    break
                
            if not keyframe_matches:
                continue
            
            times: list[float] = []
            values: list[tuple[float, float, float]] = []
            
            for keyframe_match in keyframe_matches:
                time_value = parse_float_token(keyframe_match.group(1))
                components = [
                    parse_float_token(token)
                    for token in split_top_level_csv(keyframe_match.group(2))[:3]
                ]
                
                if len(components) < 3:
                    continue
                
                if target in ("ROTATION", "ROTATE"):
                    components = [math.radians(value) for value in components]
                    
                times.append(time_value)
                values.append((components[0], components[1], components[2]))
                
            if not times:
                continue
            
            if target in ("POSITION", "TRANSLATE"):
                track.translation_times = times
                track.translation_values = values
            elif target in ("ROTATION", "ROTATE"):
                track.rotation_times = times
                track.rotation_values = values
            elif target == "SCALE":
                track.scale_times = times
                track.scale_values = values
                
        clips.append(
            AnimationClipDef(
                name=clip_name,
                length=clip_length,
                tracks=list(tracks_by_part.values()),
            )
        )
        
    return clips


def estimate_hitbox(model: CreatureModelDef) -> tuple[float, float]:
    pivot_by_name = {part.name: part.pivot for part in model.parts}
    min_x = 0.0
    max_x = 0.0
    min_y = 0.0
    max_y = 0.0

    for part in model.parts:
        px, py, _ = pivot_by_name[part.name]
        for cube in part.cubes:
            min_x = min(min_x, px + cube.x)
            max_x = max(max_x, px + cube.x + cube.dx)
            min_y = min(min_y, py + cube.y)
            max_y = max(max_y, py + cube.y + cube.dy)

    width = max(0.4, min(4.0, round((max_x - min_x) / 16.0, 3)))
    height = max(0.4, min(4.0, round((max_y - min_y) / 16.0, 3)))
    return width, height


def read_allowed_creature_ids(study_cards_java: Path) -> set[str]:
    source = study_cards_java.read_text(encoding="utf-8")
    return set(CUSTOM_CARD_RE.findall(source))


def write_manifest(entries: list[dict], output_resources_dir: Path) -> None:
    manifest_path = (
        output_resources_dir
        / "assets"
        / "study-checkpoints"
        / "study-creatures"
        / "creatures.json"
    )
    manifest_path.parent.mkdir(parents=True, exist_ok=True)
    manifest_path.write_text(
        json.dumps({"creatures": entries}, indent=2) + "\n",
        encoding="utf-8",
    )


def wipe_dir(path: Path) -> None:
    if path.exists():
        shutil.rmtree(path)
    path.mkdir(parents=True, exist_ok=True)


def copy_texture(src: Path, dest: Path) -> None:
    dest.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src, dest)


def find_png(folder: Path) -> Path | None:
    for candidate in sorted(folder.glob("*.png")):
        return candidate
    return None


def find_gltf_file(source_dir: Path) -> Path | None:
    candidates = sorted(source_dir.glob("*.gltf"))
    return candidates[0] if candidates else None


def generate_from_blockbench(creature_id: str, creature_dir: Path, java_dir: Path, texture_dir: Path) -> dict | None:
    model_java = creature_dir / "model.java"
    model_png = creature_dir / "model.png"
    if not model_java.exists() or not model_png.exists():
        return None

    source = model_java.read_text(encoding="utf-8")
    model = parse_blockbench_model(creature_id, source)

    anim_sidecar: Path | None = None
    for candidate_name in ("model-anim.java", "model-anim.txt"):
        candidate = creature_dir / candidate_name
        if candidate.exists():
            anim_sidecar = candidate
            break
        
    if anim_sidecar is not None:
        animation_source = anim_sidecar.read_text(encoding="utf-8")
        clips = parse_blockbench_animation_sidecar(animation_source)
        model.animation_clips = clips
        model.preferred_idle_clip = choose_named_clip(clips, ("idle", "base", "pose", "stand"))
        model.preferred_walk_clip = choose_named_clip(clips, ("walk", "run", "move"))
        if model.preferred_idle_clip is None and clips:
            model.preferred_idle_clip = clips[0].name
            
        if clips:
            model.animation_notes.append(f"Blockbench animation sidecar merged from {anim_sidecar.name}.")
        else:
            model.animation_notes.append(
                f"Blockbench animation sidecar found at {anim_sidecar.name}, but no clips were parsed."
            )
    hitbox_w, hitbox_h = estimate_hitbox(model)
    model.hitbox_width = hitbox_w
    model.hitbox_height = hitbox_h

    output_java = java_dir / f"{model.class_name}.java"
    output_java.parent.mkdir(parents=True, exist_ok=True)
    output_java.write_text(emit_model_java(model), encoding="utf-8")

    copy_texture(model_png, texture_dir / f"{creature_id}.png")

    return {
        "id": creature_id,
        "display_name": snake_to_pascal(creature_id).replace("_", " "),
        "hitbox_width": model.hitbox_width,
        "hitbox_height": model.hitbox_height,
        "model_class_name": f"io.github.u01234567.studycheckpoints.creatures.generated.{model.class_name}",
        "outer_model_class_name": None,
        "animation_class_name": None,
        "texture_resource_path": f"textures/entity/{creature_id}.png",
        "outer_texture_resource_path": None,
    }


def generate_from_gltf(creature_id: str, creature_dir: Path, java_dir: Path, texture_dir: Path) -> dict | None:
    from convert_gltf_to_java import convert_gltf_creature

    gltf_path = find_gltf_file(creature_dir / "source")
    textures_dir = creature_dir / "textures"
    if gltf_path is None or not textures_dir.exists():
        return None

    result = convert_gltf_creature(creature_id=creature_id, gltf_path=gltf_path, mesh_fallback="bounds")

    output_java = java_dir / f"{result.model.class_name}.java"
    output_java.parent.mkdir(parents=True, exist_ok=True)
    output_java.write_text(emit_model_java(result.model), encoding="utf-8")

    output_texture = texture_dir / f"{creature_id}.png"
    output_texture.parent.mkdir(parents=True, exist_ok=True)
    result.atlas.save(output_texture)

    outer_model_class_name = None
    outer_texture_resource_path = None
    if result.outer_model is not None and result.outer_atlas is not None:
        output_outer_java = java_dir / f"{result.outer_model.class_name}.java"
        output_outer_java.parent.mkdir(parents=True, exist_ok=True)
        output_outer_java.write_text(emit_model_java(result.outer_model), encoding="utf-8")

        output_outer_texture = texture_dir / f"{creature_id}_outer.png"
        output_outer_texture.parent.mkdir(parents=True, exist_ok=True)
        result.outer_atlas.save(output_outer_texture)

        outer_model_class_name = f"io.github.u01234567.studycheckpoints.creatures.generated.{result.outer_model.class_name}"
        outer_texture_resource_path = f"textures/entity/{creature_id}_outer.png"

    return {
        "id": creature_id,
        "display_name": snake_to_pascal(creature_id).replace("_", " "),
        "hitbox_width": result.model.hitbox_width,
        "hitbox_height": result.model.hitbox_height,
        "model_class_name": f"io.github.u01234567.studycheckpoints.creatures.generated.{result.model.class_name}",
        "outer_model_class_name": outer_model_class_name,
        "animation_class_name": None,
        "texture_resource_path": f"textures/entity/{creature_id}.png",
        "outer_texture_resource_path": outer_texture_resource_path,
    }


def iter_creature_dirs(root: Path) -> Iterable[Path]:
    if not root.exists():
        return []
    return [path for path in sorted(root.iterdir()) if path.is_dir()]


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument("--project-root", required=True)
    parser.add_argument("--output-java-dir", required=True)
    parser.add_argument("--output-resources-dir", required=True)
    parser.add_argument("--include-unlisted", action="store_true")
    parser.add_argument("--skip-gltf", action="store_true")
    args = parser.parse_args()

    project_root = Path(args.project_root).resolve()
    external_root = (project_root.parent / "external").resolve()
    study_cards_java = (
        project_root
        / "src"
        / "main"
        / "java"
        / "io"
        / "github"
        / "u01234567"
        / "studycheckpoints"
        / "StudyCreatureCards.java"
    )

    allowed_ids = read_allowed_creature_ids(study_cards_java)
    output_java_dir = Path(args.output_java_dir).resolve() / "io" / "github" / "u01234567" / "studycheckpoints" / "creatures" / "generated"
    output_resources_dir = Path(args.output_resources_dir).resolve()
    output_texture_dir = output_resources_dir / "assets" / "study-checkpoints" / "textures" / "entity"

    wipe_dir(output_java_dir)
    wipe_dir(output_texture_dir)
    manifest_dir = output_resources_dir / "assets" / "study-checkpoints" / "study-creatures"
    wipe_dir(manifest_dir)

    manifest_entries: list[dict] = []

    blockbench_root = external_root / "blockbench"
    gltf_root = external_root / "gltf"

    for creature_dir in iter_creature_dirs(blockbench_root):
        creature_id = creature_dir.name
        if not args.include_unlisted and creature_id not in allowed_ids:
            continue
        entry = generate_from_blockbench(creature_id, creature_dir, output_java_dir, output_texture_dir)
        if entry:
            manifest_entries.append(entry)

    existing_ids = {entry["id"] for entry in manifest_entries}
    if not args.skip_gltf:
        for creature_dir in iter_creature_dirs(gltf_root):
            creature_id = creature_dir.name
            if creature_id in existing_ids:
                continue
            if not args.include_unlisted and creature_id not in allowed_ids:
                continue
            entry = generate_from_gltf(creature_id, creature_dir, output_java_dir, output_texture_dir)
            if entry:
                manifest_entries.append(entry)

    manifest_entries.sort(key=lambda entry: entry["id"])
    write_manifest(manifest_entries, output_resources_dir)

    print(f"Generated {len(manifest_entries)} study creature definitions.")
    for entry in manifest_entries:
        print(f" - {entry['id']} -> {entry['model_class_name']}")


if __name__ == "__main__":
    main()