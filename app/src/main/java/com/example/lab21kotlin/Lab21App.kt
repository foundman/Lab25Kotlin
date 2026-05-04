package com.example.lab21kotlin

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class с аннотацией @HiltAndroidApp.
 * Инициализирует граф зависимостей Hilt на уровне приложения.
 */
@HiltAndroidApp
class Lab21App : Application()