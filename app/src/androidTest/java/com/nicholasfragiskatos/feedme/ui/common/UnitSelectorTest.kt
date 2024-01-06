package com.nicholasfragiskatos.feedme.ui.common

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.google.common.truth.Truth.assertThat
import com.nicholasfragiskatos.feedme.domain.model.UnitOfMeasurement
import org.junit.Rule
import org.junit.Test

class UnitSelectorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun unitSelector_radioButtonsExist() {
        composeTestRule.setContent {
            UnitSelector(selectedUnit = UnitOfMeasurement.MILLILITER, onSelect = {})
        }

        composeTestRule
            .onNodeWithText(UnitOfMeasurement.OUNCE.abbreviation, ignoreCase = true)
            .assertExists()

        composeTestRule
            .onNodeWithText(UnitOfMeasurement.MILLILITER.abbreviation, ignoreCase = true)
            .assertExists()

    }

    @Test
    fun unitSelector_selectedUnitParameterOunce() {
        composeTestRule.setContent {

            UnitSelector(selectedUnit = UnitOfMeasurement.OUNCE, onSelect = {})

        }

        composeTestRule
            .onNodeWithText(UnitOfMeasurement.OUNCE.abbreviation, ignoreCase = true)
            .assertIsSelected()
    }

    @Test
    fun unitSelector_selectedUnitParameterMilliliter() {
        composeTestRule.setContent {

            UnitSelector(selectedUnit = UnitOfMeasurement.MILLILITER, onSelect = {})

        }

        composeTestRule
            .onNodeWithText(UnitOfMeasurement.MILLILITER.abbreviation, ignoreCase = true)
            .assertIsSelected()
    }

    @Test
    fun unitSelector_radioButtonOnSelect() {
        val units = mutableListOf<UnitOfMeasurement>()
        composeTestRule.setContent {
            UnitSelector(selectedUnit = UnitOfMeasurement.OUNCE, onSelect = {
                units.add(it)
            })
        }

        composeTestRule
            .onNodeWithText(UnitOfMeasurement.MILLILITER.abbreviation, ignoreCase = true)
            .performClick()

        composeTestRule
            .onNodeWithText(UnitOfMeasurement.OUNCE.abbreviation, ignoreCase = true)
            .performClick()

        assertThat(units).isNotEmpty()
        assertThat(units).hasSize(2)
        assertThat(units[0]).isEqualTo(UnitOfMeasurement.MILLILITER)
        assertThat(units[1]).isEqualTo(UnitOfMeasurement.OUNCE)
    }
}