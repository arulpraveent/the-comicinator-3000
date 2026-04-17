package com.deepvisiontech.thecomicinator3000.features.onboarding.presentation.utils

import com.deepvisiontech.thecomicinator3000.R
import com.deepvisiontech.thecomicinator3000.core.domain.model.AppError
import com.deepvisiontech.thecomicinator3000.core.domain.model.UiText

fun AppError.asOnBoardingUiText(): UiText {
    return when (this) {
        AppError.Database -> UiText.StringResource(
            R.string.onboarding_error_database
        )
        AppError.Validation -> UiText.StringResource(
            R.string.onboarding_error_validation
        )
        else -> UiText.StringResource(
            R.string.onboarding_error_unknown
        )
    }
}