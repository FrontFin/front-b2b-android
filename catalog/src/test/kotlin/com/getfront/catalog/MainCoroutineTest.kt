package com.getfront.catalog

import org.junit.Rule

open class MainCoroutineTest {
    @get:Rule
    val mainCoroutineRule = MainDispatcherRule()
}