package com.deepvisiontech.thecomicinator3000.features.common.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

val Context.storageAccessDataStore: DataStore<Preferences> by preferencesDataStore(name = "storage_access_store")