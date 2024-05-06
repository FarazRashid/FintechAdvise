package com.se.fintechadvise.ManagerClasses

object PersonalisedLearningManager {
    private var isPersonalisedLearningEnabled = false

    fun isPersonalisedLearningEnabled(): Boolean {
        return isPersonalisedLearningEnabled
    }
    fun setPersonalisedLearningEnabled(isEnabled: Boolean) {
        isPersonalisedLearningEnabled = isEnabled
    }
}