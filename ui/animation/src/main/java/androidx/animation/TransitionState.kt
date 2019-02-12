/*
 * Copyright 2019 The Android Open Source Project
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

package androidx.animation

import androidx.ui.core.Dp
import androidx.ui.core.Px
import androidx.ui.core.lerp
import androidx.ui.lerp
import androidx.ui.painting.Color

internal open class StateImpl(val name: Any) : MutableTransitionState, TransitionState {

    internal val props: MutableMap<PropKey<out Any>, Any> = mutableMapOf()

    override operator fun set(name: PropKey<out Any>, prop: Any) {
        if (props[name] != null) {
            throw IllegalArgumentException("prop name $name already exists")
        }

        props[name] = prop
    }

    override operator fun <T : Any> get(propKey: PropKey<T>): T {
        return props[propKey] as T
    }
}

/**
 * [TransitionState] holds a number of property values. The value of a property can be queried via
 * [get], providing its property key.
 */
interface TransitionState {
    operator fun <T : Any> get(prop: PropKey<T>): T
}

/**
 * [MutableTransitionState] is used in [TransitionDefinition] for constructing various
 * [TransitionState]s with corresponding properties and their values.
 */
interface MutableTransitionState {
    operator fun set(name: PropKey<out Any>, prop: Any)
}

/**
 * Property key of [T] type.
 */
interface PropKey<T : Any> {
    fun interpolate(a: T, b: T, fraction: Float): T
}

/**
 * Built-in property key for color properties.
 */
class ColorPropKey : PropKey<Color> {
    override fun interpolate(a: Color, b: Color, fraction: Float): Color {
        return Color.blend(a, b, fraction)
    }
}

/**
 * Built-in property key for float properties.
 */
class FloatPropKey : PropKey<Float> {
    override fun interpolate(a: Float, b: Float, fraction: Float) =
        lerp(a, b, fraction)
}

// TODO: refactor out the entirely independent bit of the animation engine
/**
 * Built-in property key for int properties.
 */
class IntPropKey : PropKey<Int> {
    override fun interpolate(a: Int, b: Int, fraction: Float) =
        lerp(a, b, fraction).toInt()
}

/**
 * Built-in property key for Dp properties.
 */
class PxPropKey : PropKey<Px> {
    override fun interpolate(a: Px, b: Px, fraction: Float): Px =
        lerp(a, b, fraction)
}

/**
 * Built-in property key for Dp properties.
 */
class DpPropKey : PropKey<Dp> {
    override fun interpolate(a: Dp, b: Dp, fraction: Float): Dp =
        lerp(a, b, fraction)
}
