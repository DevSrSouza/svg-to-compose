/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.material.icons.generator.vector

import androidx.compose.material.icons.generator.ScaleFactor
import com.google.common.math.Quantiles.Scale

/**
 * Class representing a singular path command in a vector.
 *
 * @property isCurve whether this command is a curve command
 * @property isQuad whether this command is a quad command
 */
/* ktlint-disable max-line-length */
sealed class PathNode(val isCurve: Boolean = false, val isQuad: Boolean = false) {
    /**
     * Maps a [PathNode] to a string representing an invocation of the corresponding PathBuilder
     * function to add this node to the builder.
     */
    abstract fun asFunctionCall(): String

    // RelativeClose and Close are considered the same internally, so we represent both with Close
    // for simplicity and to make equals comparisons robust.
    object Close : PathNode() {
        override fun asFunctionCall() = "close()"
    }

    data class RelativeMoveTo(val x: Float, val y: Float) : PathNode() {
        override fun asFunctionCall() = "moveToRelative(${x}f, ${y}f)"
    }

    data class MoveTo(val x: Float, val y: Float) : PathNode() {
        override fun asFunctionCall() = "moveTo(${x}f, ${y}f)"
    }

    data class RelativeLineTo(val x: Float, val y: Float) : PathNode() {
        override fun asFunctionCall() = "lineToRelative(${x}f, ${y}f)"
    }

    data class LineTo(val x: Float, val y: Float) : PathNode() {
        override fun asFunctionCall() = "lineTo(${x}f, ${y}f)"
    }

    data class RelativeHorizontalTo(val x: Float) : PathNode() {
        override fun asFunctionCall() = "horizontalLineToRelative(${x}f)"
    }

    data class HorizontalTo(val x: Float) : PathNode() {
        override fun asFunctionCall() = "horizontalLineTo(${x}f)"
    }

    data class RelativeVerticalTo(val y: Float) : PathNode() {
        override fun asFunctionCall() = "verticalLineToRelative(${y}f)"
    }

    data class VerticalTo(val y: Float) : PathNode() {
        override fun asFunctionCall() = "verticalLineTo(${y}f)"
    }

    data class RelativeCurveTo(
        val dx1: Float,
        val dy1: Float,
        val dx2: Float,
        val dy2: Float,
        val dx3: Float,
        val dy3: Float
    ) : PathNode(isCurve = true) {
        override fun asFunctionCall() = "curveToRelative(${dx1}f, ${dy1}f, ${dx2}f, ${dy2}f, ${dx3}f, ${dy3}f)"
    }

    data class CurveTo(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float,
        val x3: Float,
        val y3: Float
    ) : PathNode(isCurve = true) {
        override fun asFunctionCall() = "curveTo(${x1}f, ${y1}f, ${x2}f, ${y2}f, ${x3}f, ${y3}f)"
    }

    data class RelativeReflectiveCurveTo(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float
    ) : PathNode(isCurve = true) {
        override fun asFunctionCall() = "reflectiveCurveToRelative(${x1}f, ${y1}f, ${x2}f, ${y2}f)"
    }

    data class ReflectiveCurveTo(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float
    ) : PathNode(isCurve = true) {
        override fun asFunctionCall() = "reflectiveCurveTo(${x1}f, ${y1}f, ${x2}f, ${y2}f)"
    }

    data class RelativeQuadTo(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float
    ) : PathNode(isQuad = true) {
        override fun asFunctionCall() = "quadToRelative(${x1}f, ${y1}f, ${x2}f, ${y2}f)"
    }

    data class QuadTo(
        val x1: Float,
        val y1: Float,
        val x2: Float,
        val y2: Float
    ) : PathNode(isQuad = true) {
        override fun asFunctionCall() = "quadTo(${x1}f, ${y1}f, ${x2}f, ${y2}f)"
    }

    data class RelativeReflectiveQuadTo(
        val x: Float,
        val y: Float
    ) : PathNode(isQuad = true) {
        override fun asFunctionCall() = "reflectiveQuadToRelative(${x}f, ${y}f)"
    }

    data class ReflectiveQuadTo(
        val x: Float,
        val y: Float
    ) : PathNode(isQuad = true) {
        override fun asFunctionCall() = "reflectiveQuadTo(${x}f, ${y}f)"
    }

    data class RelativeArcTo(
        val horizontalEllipseRadius: Float,
        val verticalEllipseRadius: Float,
        val theta: Float,
        val isMoreThanHalf: Boolean,
        val isPositiveArc: Boolean,
        val arcStartDx: Float,
        val arcStartDy: Float
    ) : PathNode() {
        override fun asFunctionCall() =
            "arcToRelative(${horizontalEllipseRadius}f, ${verticalEllipseRadius}f, ${theta}f, $isMoreThanHalf, $isPositiveArc, ${arcStartDx}f, ${arcStartDy}f)"
    }

    data class ArcTo(
        val horizontalEllipseRadius: Float,
        val verticalEllipseRadius: Float,
        val theta: Float,
        val isMoreThanHalf: Boolean,
        val isPositiveArc: Boolean,
        val arcStartX: Float,
        val arcStartY: Float
    ) : PathNode() {
        override fun asFunctionCall() =
            "arcTo(${horizontalEllipseRadius}f, ${verticalEllipseRadius}f, ${theta}f, $isMoreThanHalf, $isPositiveArc, ${arcStartX}f, ${arcStartY}f)"
    }
}
/* ktlint-enable max-line-length */

/**
 * Return the corresponding [PathNode] for the given character key if it exists.
 * If the key is unknown then [IllegalArgumentException] is thrown
 * @return [PathNode] that matches the key
 * @throws IllegalArgumentException
 */
internal fun Char.toPathNodes(args: FloatArray, scale: ScaleFactor = ScaleFactor()): List<PathNode> = when (this) {
    RelativeCloseKey, CloseKey -> listOf(
        PathNode.Close
    )
    RelativeMoveToKey -> pathNodesFromArgs(
        args,
        NUM_MOVE_TO_ARGS
    ) { array ->
        PathNode.RelativeMoveTo(
            x = array[0] * scale.x,
            y = array[1] * scale.y
        )
    }

    MoveToKey -> pathNodesFromArgs(
        args,
        NUM_MOVE_TO_ARGS
    ) { array ->
        PathNode.MoveTo(
            x = array[0] * scale.x,
            y = array[1] * scale.y
        )
    }

    RelativeLineToKey -> pathNodesFromArgs(
        args,
        NUM_LINE_TO_ARGS
    ) { array ->
        PathNode.RelativeLineTo(
            x = array[0] * scale.x,
            y = array[1] * scale.y
        )
    }

    LineToKey -> pathNodesFromArgs(
        args,
        NUM_LINE_TO_ARGS
    ) { array ->
        PathNode.LineTo(
            x = array[0] * scale.x,
            y = array[1] * scale.y
        )
    }

    RelativeHorizontalToKey -> pathNodesFromArgs(
        args,
        NUM_HORIZONTAL_TO_ARGS
    ) { array ->
        PathNode.RelativeHorizontalTo(
            x = array[0] * scale.x
        )
    }

    HorizontalToKey -> pathNodesFromArgs(
        args,
        NUM_HORIZONTAL_TO_ARGS
    ) { array ->
        PathNode.HorizontalTo(x = array[0] * scale.x)
    }

    RelativeVerticalToKey -> pathNodesFromArgs(
        args,
        NUM_VERTICAL_TO_ARGS
    ) { array ->
        PathNode.RelativeVerticalTo(y = array[0] * scale.y)
    }

    VerticalToKey -> pathNodesFromArgs(
        args,
        NUM_VERTICAL_TO_ARGS
    ) { array ->
        PathNode.VerticalTo(y = array[0] * scale.y)
    }

    RelativeCurveToKey -> pathNodesFromArgs(
        args,
        NUM_CURVE_TO_ARGS
    ) { array ->
        PathNode.RelativeCurveTo(
            dx1 = array[0] * scale.x,
            dy1 = array[1] * scale.y,
            dx2 = array[2] * scale.x,
            dy2 = array[3] * scale.y,
            dx3 = array[4] * scale.x,
            dy3 = array[5] * scale.y
        )
    }

    CurveToKey -> pathNodesFromArgs(
        args,
        NUM_CURVE_TO_ARGS
    ) { array ->
        PathNode.CurveTo(
            x1 = array[0] * scale.x,
            y1 = array[1] * scale.y,
            x2 = array[2] * scale.x,
            y2 = array[3] * scale.y,
            x3 = array[4] * scale.x,
            y3 = array[5] * scale.y
        )
    }

    RelativeReflectiveCurveToKey -> pathNodesFromArgs(
        args,
        NUM_REFLECTIVE_CURVE_TO_ARGS
    ) { array ->
        PathNode.RelativeReflectiveCurveTo(
            x1 = array[0] * scale.x,
            y1 = array[1] * scale.y,
            x2 = array[2] * scale.x,
            y2 = array[3] * scale.y
        )
    }

    ReflectiveCurveToKey -> pathNodesFromArgs(
        args,
        NUM_REFLECTIVE_CURVE_TO_ARGS
    ) { array ->
        PathNode.ReflectiveCurveTo(
            x1 = array[0] * scale.x,
            y1 = array[1] * scale.y,
            x2 = array[2] * scale.x,
            y2 = array[3] * scale.y
        )
    }

    RelativeQuadToKey -> pathNodesFromArgs(
        args,
        NUM_QUAD_TO_ARGS
    ) { array ->
        PathNode.RelativeQuadTo(
            x1 = array[0] * scale.x,
            y1 = array[1] * scale.y,
            x2 = array[2] * scale.x,
            y2 = array[3] * scale.y
        )
    }

    QuadToKey -> pathNodesFromArgs(
        args,
        NUM_QUAD_TO_ARGS
    ) { array ->
        PathNode.QuadTo(
            x1 = array[0] * scale.x,
            y1 = array[1] * scale.y,
            x2 = array[2] * scale.x,
            y2 = array[3] * scale.y
        )
    }

    RelativeReflectiveQuadToKey -> pathNodesFromArgs(
        args,
        NUM_REFLECTIVE_QUAD_TO_ARGS
    ) { array ->
        PathNode.RelativeReflectiveQuadTo(
            x = array[0] * scale.x,
            y = array[1] * scale.y
        )
    }

    ReflectiveQuadToKey -> pathNodesFromArgs(
        args,
        NUM_REFLECTIVE_QUAD_TO_ARGS
    ) { array ->
        PathNode.ReflectiveQuadTo(
            x = array[0] * scale.x,
            y = array[1] * scale.y
        )
    }

    RelativeArcToKey -> pathNodesFromArgs(
        args,
        NUM_ARC_TO_ARGS
    ) { array ->
        PathNode.RelativeArcTo(
            horizontalEllipseRadius = array[0] * scale.x,
            verticalEllipseRadius = array[1] * scale.y,
            theta = array[2],
            isMoreThanHalf = (array[3] * scale.x).compareTo(0.0f) != 0,
            isPositiveArc = (array[4] * scale.y).compareTo(0.0f) != 0,
            arcStartDx = array[5] * scale.x,
            arcStartDy = array[6] * scale.y
        )
    }

    ArcToKey -> pathNodesFromArgs(
        args,
        NUM_ARC_TO_ARGS
    ) { array ->
        PathNode.ArcTo(
            horizontalEllipseRadius = array[0] * scale.x,
            verticalEllipseRadius = array[1] * scale.y,
            theta = array[2],
            isMoreThanHalf = (array[3] * scale.x).compareTo(0.0f) != 0,
            isPositiveArc = (array[4] * scale.y).compareTo(0.0f) != 0,
            arcStartX = array[5] * scale.x,
            arcStartY = array[6] * scale.y
        )
    }

    else -> throw IllegalArgumentException("Unknown command for: $this")
}

private inline fun pathNodesFromArgs(
    args: FloatArray,
    numArgs: Int,
    scale: ScaleFactor = ScaleFactor(),
    nodeFor: (subArray: FloatArray) -> PathNode
): List<PathNode> {
    return (0..args.size - numArgs step numArgs).map { index ->
        val subArray = args.slice(index until index + numArgs).toFloatArray()
        val node = nodeFor(subArray)
        when {
            // According to the spec, if a MoveTo is followed by multiple pairs of coordinates,
            // the subsequent pairs are treated as implicit corresponding LineTo commands.
            node is PathNode.MoveTo && index > 0 -> PathNode.LineTo(
                subArray[0] * scale.x,
                subArray[1] * scale.y
            )
            node is PathNode.RelativeMoveTo && index > 0 ->
                PathNode.RelativeLineTo(
                    subArray[0] * scale.x,
                    subArray[1] * scale.y
                )
            else -> node
        }
    }
}

/**
 * Constants used by [Char.toPathNodes] for creating [PathNode]s from parsed paths.
 */
private const val RelativeCloseKey = 'z'
private const val CloseKey = 'Z'
private const val RelativeMoveToKey = 'm'
private const val MoveToKey = 'M'
private const val RelativeLineToKey = 'l'
private const val LineToKey = 'L'
private const val RelativeHorizontalToKey = 'h'
private const val HorizontalToKey = 'H'
private const val RelativeVerticalToKey = 'v'
private const val VerticalToKey = 'V'
private const val RelativeCurveToKey = 'c'
private const val CurveToKey = 'C'
private const val RelativeReflectiveCurveToKey = 's'
private const val ReflectiveCurveToKey = 'S'
private const val RelativeQuadToKey = 'q'
private const val QuadToKey = 'Q'
private const val RelativeReflectiveQuadToKey = 't'
private const val ReflectiveQuadToKey = 'T'
private const val RelativeArcToKey = 'a'
private const val ArcToKey = 'A'

/**
 * Constants for the number of expected arguments for a given node. If the number of received
 * arguments is a multiple of these, the excess will be converted into additional path nodes.
 */
private const val NUM_MOVE_TO_ARGS = 2
private const val NUM_LINE_TO_ARGS = 2
private const val NUM_HORIZONTAL_TO_ARGS = 1
private const val NUM_VERTICAL_TO_ARGS = 1
private const val NUM_CURVE_TO_ARGS = 6
private const val NUM_REFLECTIVE_CURVE_TO_ARGS = 4
private const val NUM_QUAD_TO_ARGS = 4
private const val NUM_REFLECTIVE_QUAD_TO_ARGS = 2
private const val NUM_ARC_TO_ARGS = 7
