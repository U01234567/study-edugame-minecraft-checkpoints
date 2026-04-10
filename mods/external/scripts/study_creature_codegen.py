from __future__ import annotations

from dataclasses import dataclass, field
from typing import Iterable


PACKAGE_NAME = "io.github.u01234567.studycheckpoints.creatures.generated"


@dataclass
class CubeDef:
    u: int
    v: int
    x: float
    y: float
    z: float
    dx: float
    dy: float
    dz: float
    deformation: float = 0.0


@dataclass
class PartDef:
    name: str
    parent: str | None
    pivot: tuple[float, float, float] = (0.0, 0.0, 0.0)
    rotation: tuple[float, float, float] = (0.0, 0.0, 0.0)
    cubes: list[CubeDef] = field(default_factory=list)


@dataclass
class AnimationTrackDef:
    part_name: str
    translation_times: list[float] = field(default_factory=list)
    translation_values: list[tuple[float, float, float]] = field(default_factory=list)
    rotation_times: list[float] = field(default_factory=list)
    rotation_values: list[tuple[float, float, float]] = field(default_factory=list)
    scale_times: list[float] = field(default_factory=list)
    scale_values: list[tuple[float, float, float]] = field(default_factory=list)


@dataclass
class AnimationClipDef:
    name: str
    length: float
    tracks: list[AnimationTrackDef] = field(default_factory=list)


@dataclass
class CreatureModelDef:
    creature_id: str
    class_name: str
    texture_width: int
    texture_height: int
    parts: list[PartDef]
    animation_notes: list[str] = field(default_factory=list)
    animation_clips: list[AnimationClipDef] = field(default_factory=list)
    preferred_idle_clip: str | None = None
    preferred_walk_clip: str | None = None
    hitbox_width: float = 2.8
    hitbox_height: float = 1.8


def snake_to_pascal(value: str) -> str:
    return "".join(part[:1].upper() + part[1:] for part in value.replace("-", "_").split("_") if part)


def sanitize_java_identifier(value: str) -> str:
    cleaned = []
    for ch in value:
        cleaned.append(ch if (ch.isalnum() or ch == "_") else "_")
    result = "".join(cleaned).strip("_")
    if not result:
        result = "part"
    if result[0].isdigit():
        result = f"p_{result}"
    return result


def uniquify(names: Iterable[str]) -> list[str]:
    used: dict[str, int] = {}
    out: list[str] = []
    for name in names:
        base = sanitize_java_identifier(name)
        count = used.get(base, 0)
        used[base] = count + 1
        out.append(base if count == 0 else f"{base}_{count + 1}")
    return out


def fmtf(value: float) -> str:
    rounded = round(float(value), 4)
    text = f"{rounded:.4f}".rstrip("0").rstrip(".")
    if "." not in text:
        text += ".0"
    return f"{text}F"


def sanitize_java_constant(value: str) -> str:
    cleaned = []
    for ch in value:
        cleaned.append(ch if ch.isalnum() else "_")
    result = "".join(cleaned).strip("_").upper()
    if not result:
        result = "VALUE"
    if result[0].isdigit():
        result = f"N_{result}"
    return result


def emit_float_array(values: list[float]) -> str:
    if not values:
        return "new float[0]"
    return "new float[]{" + ", ".join(fmtf(value) for value in values) + "}"


def emit_vec3_array(values: list[tuple[float, float, float]]) -> str:
    flattened: list[float] = []
    for x, y, z in values:
        flattened.extend([x, y, z])
    return emit_float_array(flattened)


def emit_model_java(model: CreatureModelDef) -> str:
    if not model.parts:
        raise ValueError(f"{model.creature_id}: model has no parts")

    java_names = uniquify([part.name for part in model.parts])
    name_by_part = {id(part): java_name for part, java_name in zip(model.parts, java_names)}

    part_by_name = {part.name: part for part in model.parts}
    java_by_name = {part.name: name_by_part[id(part)] for part in model.parts}

    if "root" not in part_by_name:
        raise ValueError(f"{model.creature_id}: model is missing synthetic root part named 'root'")

    constructor_lines = [
        "        super(root);",
        '        this.root = root.getChild("root");',
    ]

    for part in model.parts:
        if part.name == "root":
            continue
        java_name = java_by_name[part.name]
        parent_name = part.parent
        if parent_name is None:
            parent_accessor = "root"
        elif parent_name == "root":
            parent_accessor = "this.root"
        else:
            parent_accessor = f"this.{java_by_name[parent_name]}"
        constructor_lines.append(
            f'        this.{java_name} = {parent_accessor}.getChild("{part.name}");'
        )

    method_lines = [
        "    public static LayerDefinition getLayerDefinition() {",
        "        return getTexturedModelData();",
        "    }",
        "",
        "    public static LayerDefinition getTexturedModelData() {",
        "        MeshDefinition meshDefinition = new MeshDefinition();",
        "        PartDefinition partDefinition = meshDefinition.getRoot();",
        "",
    ]

    local_var_by_name: dict[str, str] = {"<mesh-root>": "partDefinition"}
    for part in model.parts:
        local_name = java_by_name[part.name]
        parent_local = "partDefinition" if part.parent is None else local_var_by_name[part.parent]

        builder = "CubeListBuilder.create()"
        for cube in part.cubes:
            builder += (
                f".texOffs({cube.u}, {cube.v})"
                f".addBox({fmtf(cube.x)}, {fmtf(cube.y)}, {fmtf(cube.z)}, "
                f"{fmtf(cube.dx)}, {fmtf(cube.dy)}, {fmtf(cube.dz)}, new CubeDeformation({fmtf(cube.deformation)}))"
            )

        px, py, pz = part.pivot
        rx, ry, rz = part.rotation
        if abs(rx) < 1e-6 and abs(ry) < 1e-6 and abs(rz) < 1e-6:
            pose = f"PartPose.offset({fmtf(px)}, {fmtf(py)}, {fmtf(pz)})"
        else:
            pose = (
                "PartPose.offsetAndRotation("
                f"{fmtf(px)}, {fmtf(py)}, {fmtf(pz)}, "
                f"{fmtf(rx)}, {fmtf(ry)}, {fmtf(rz)})"
            )

        method_lines.append(
            f'        PartDefinition {local_name} = {parent_local}.addOrReplaceChild("{part.name}", {builder}, {pose});'
        )
        local_var_by_name[part.name] = local_name

    method_lines.extend(
        [
            "",
            f"        return LayerDefinition.create(meshDefinition, {model.texture_width}, {model.texture_height});",
            "    }",
            "",
            "    private static float wrapAnimationTime(float time, float length) {",
            "        if (length <= 0.0F) {",
            "            return 0.0F;",
            "        }",
            "        float wrapped = time % length;",
            "        return wrapped < 0.0F ? wrapped + length : wrapped;",
            "    }",
            "",
            "    private static float sampleChannel(float[] times, float[] values, int stride, int component, float time) {",
            "        if (times.length == 0) {",
            "            return 0.0F;",
            "        }",
            "        if (times.length == 1 || time <= times[0]) {",
            "            return values[component];",
            "        }",
            "        for (int i = 0; i < times.length - 1; i++) {",
            "            float start = times[i];",
            "            float end = times[i + 1];",
            "            if (time <= end) {",
            "                int base = (i * stride) + component;",
            "                if (end <= start + 1.0E-6F) {",
            "                    return values[base + stride];",
            "                }",
            "                float delta = (time - start) / (end - start);",
            "                return values[base] + ((values[base + stride] - values[base]) * delta);",
            "            }",
            "        }",
            "        return values[((times.length - 1) * stride) + component];",
            "    }",
            "",
            "    private static void applyTranslationTrack(ModelPart part, float[] times, float[] values, float time) {",
            "        if (times.length == 0) {",
            "            return;",
            "        }",
            "        part.x += sampleChannel(times, values, 3, 0, time);",
            "        part.y += sampleChannel(times, values, 3, 1, time);",
            "        part.z += sampleChannel(times, values, 3, 2, time);",
            "    }",
            "",
            "    private static void applyRotationTrack(ModelPart part, float[] times, float[] values, float time) {",
            "        if (times.length == 0) {",
            "            return;",
            "        }",
            "        part.xRot += sampleChannel(times, values, 3, 0, time);",
            "        part.yRot += sampleChannel(times, values, 3, 1, time);",
            "        part.zRot += sampleChannel(times, values, 3, 2, time);",
            "    }",
            "",
            "    private static void applyScaleTrack(ModelPart part, float[] times, float[] values, float time) {",
            "        if (times.length == 0) {",
            "            return;",
            "        }",
            "        part.xScale *= sampleChannel(times, values, 3, 0, time);",
            "        part.yScale *= sampleChannel(times, values, 3, 1, time);",
            "        part.zScale *= sampleChannel(times, values, 3, 2, time);",
            "    }",
            "",
            "    private void applyGeneratedAnimation(LivingEntityRenderState state) {",
            "        this.root.getAllParts().forEach(ModelPart::resetPose);",
        ]
    )

    if model.animation_notes:
        for note in model.animation_notes:
            safe_note = note.replace("*/", "* /")
            method_lines.append(f"        // {safe_note}")

    clip_name_to_const: dict[str, str] = {}
    animation_field_lines: list[str] = []
    animation_clip_methods: list[str] = []

    for clip in model.animation_clips:
        clip_const = sanitize_java_constant(clip.name)
        clip_name_to_const[clip.name] = clip_const
        animation_field_lines.append(f"    private static final float {clip_const}_LENGTH = {fmtf(clip.length)};")

        clip_method: list[str] = [
            "",
            f"    private void applyClip{clip_const}(float time) {{",
            f"        float wrappedTime = wrapAnimationTime(time, {clip_const}_LENGTH);",
        ]

        for track in clip.tracks:
            if track.part_name not in java_by_name:
                continue

            part_ref = "this.root" if track.part_name == "root" else f"this.{java_by_name[track.part_name]}"
            part_const = sanitize_java_constant(java_by_name[track.part_name])

            if track.translation_times:
                times_name = f"{clip_const}_{part_const}_TRANSLATION_TIMES"
                values_name = f"{clip_const}_{part_const}_TRANSLATION_VALUES"
                animation_field_lines.append(f"    private static final float[] {times_name} = {emit_float_array(track.translation_times)};")
                animation_field_lines.append(f"    private static final float[] {values_name} = {emit_vec3_array(track.translation_values)};")
                clip_method.append(f"        applyTranslationTrack({part_ref}, {times_name}, {values_name}, wrappedTime);")

            if track.rotation_times:
                times_name = f"{clip_const}_{part_const}_ROTATION_TIMES"
                values_name = f"{clip_const}_{part_const}_ROTATION_VALUES"
                animation_field_lines.append(f"    private static final float[] {times_name} = {emit_float_array(track.rotation_times)};")
                animation_field_lines.append(f"    private static final float[] {values_name} = {emit_vec3_array(track.rotation_values)};")
                clip_method.append(f"        applyRotationTrack({part_ref}, {times_name}, {values_name}, wrappedTime);")

            if track.scale_times:
                times_name = f"{clip_const}_{part_const}_SCALE_TIMES"
                values_name = f"{clip_const}_{part_const}_SCALE_VALUES"
                animation_field_lines.append(f"    private static final float[] {times_name} = {emit_float_array(track.scale_times)};")
                animation_field_lines.append(f"    private static final float[] {values_name} = {emit_vec3_array(track.scale_values)};")
                clip_method.append(f"        applyScaleTrack({part_ref}, {times_name}, {values_name}, wrappedTime);")

        clip_method.append("    }")
        animation_clip_methods.extend(clip_method)

    idle_clip = model.preferred_idle_clip or (model.animation_clips[0].name if model.animation_clips else None)
    walk_clip = model.preferred_walk_clip

    if idle_clip and idle_clip in clip_name_to_const:
        method_lines.append("        float idleTimeSeconds = state.ageInTicks / 20.0F;")
        method_lines.append("        float walkTimeSeconds = state.walkAnimationPos / 20.0F;")
        if walk_clip and walk_clip in clip_name_to_const and walk_clip != idle_clip:
            method_lines.append(f"        if (state.walkAnimationSpeed > 0.12F) {{")
            method_lines.append(f"            applyClip{clip_name_to_const[walk_clip]}(walkTimeSeconds);")
            method_lines.append("        } else {")
            method_lines.append(f"            applyClip{clip_name_to_const[idle_clip]}(idleTimeSeconds);")
            method_lines.append("        }")
        else:
            method_lines.append(f"        applyClip{clip_name_to_const[idle_clip]}(idleTimeSeconds);")
    method_lines.append("    }")
    method_lines.append("")
    method_lines.append("    @Override")
    method_lines.append("    public void setupAnim(LivingEntityRenderState state) {")
    method_lines.append("        applyGeneratedAnimation(state);")
    method_lines.append("    }")
    method_lines.append("")
    method_lines.append("    public void setAngles(LivingEntityRenderState state) {")
    method_lines.append("        applyGeneratedAnimation(state);")
    method_lines.append("    }")

    field_lines = ["    private final ModelPart root;"]
    for part in model.parts:
        if part.name == "root":
            continue
        field_lines.append(f"    private final ModelPart {java_by_name[part.name]};")
    field_lines.extend(animation_field_lines)

    return f"""\
package {PACKAGE_NAME};

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/**
 * Generated from external creature assets.
 * Creature id: {model.creature_id}
 */
public class {model.class_name} extends EntityModel<LivingEntityRenderState> {{
{chr(10).join(field_lines)}

    public {model.class_name}(ModelPart root) {{
{chr(10).join(constructor_lines)}
    }}

{chr(10).join(method_lines)}
{chr(10).join(animation_clip_methods)}
}}
"""