package com.nicholasfragiskatos.feedme.utils

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.saveable.mapSaver

val TransitionStateSaver = run {
    val currentStateKey = "CurrentState"
    val targetStateKey = "TargetState"

    mapSaver(
        restore = {
            MutableTransitionState(it[currentStateKey] as Boolean).apply {
                targetState = it[targetStateKey] as Boolean
            }
        },
        save = {
            mapOf(
                currentStateKey to it.currentState,
                targetStateKey to it.targetState,
            )
        },
    )
}