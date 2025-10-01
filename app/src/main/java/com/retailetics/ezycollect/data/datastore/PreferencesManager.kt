package com.retailetics.ezycollect.data.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.retailetics.ezycollect.data.datastore.model.UserPreferences
import com.retailetics.ezycollect.data.remote.dto.LoginResponse
import com.retailetics.ezycollect.domain.model.AppMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import okio.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val MERCHANT_ID = stringPreferencesKey("merchant_id")
        private val OUTLET_ID = stringPreferencesKey("outlet_id")
        private val IS_DEVICE_ACTIVATED = booleanPreferencesKey("is_device_activated")
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val APP_MODE = stringPreferencesKey("app_mode")
        private val SHOPPING_CART_ID = stringPreferencesKey("cart_id")

        private val X_AUTH_TOKEN = stringPreferencesKey("x_auth_token")
        private val ALLOW_EZY_LITE = booleanPreferencesKey("allowEzycartLite")
        private val EMPLOYEE_EMAIL = stringPreferencesKey("email")
        private val EMPLOYEE_PIN = stringPreferencesKey("employeePin")
        private val EMPLOYEE_NAME = stringPreferencesKey("employeeName")
        private val BIOMETRIC_ENABLED = intPreferencesKey("biometricEnabled")
    }

    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                allowEzyCartLite = preferences[ALLOW_EZY_LITE] ?: false,
                employeeEmail = preferences[EMPLOYEE_EMAIL] ?: "",
                xAuthToken = preferences[X_AUTH_TOKEN] ?: "",
                employeePin = preferences[EMPLOYEE_PIN] ?: "",
                employeeName = preferences[EMPLOYEE_NAME] ?: "",

            )
        }
    suspend fun saveEmployeeDetails(data: LoginResponse) {
        dataStore.edit { preferences ->
            preferences[EMPLOYEE_EMAIL] = data.merchant.email
            preferences[X_AUTH_TOKEN] = data.token
            preferences[AUTH_TOKEN] = data.token
            preferences[EMPLOYEE_PIN] = data.merchant.login_pin
            preferences[EMPLOYEE_NAME] = data.merchant.full_name
            preferences[MERCHANT_ID] = "${data.merchant.id}"
            preferences[BIOMETRIC_ENABLED] = data.merchant.biometric_enabled
        }
          saveXAuthToken(data.token)
    }
    suspend fun saveCartId(cartId:String){
        dataStore.edit { preferences ->
            preferences[SHOPPING_CART_ID] = cartId
        }
    }

    suspend fun saveAuthToken(token:String){
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN] = token
        }
    }
    suspend fun saveXAuthToken(token:String){
        dataStore.edit { preferences ->
            preferences[X_AUTH_TOKEN] = token
        }
    }
    suspend fun saveMerchantId(id:String){
        dataStore.edit { preferences ->
            preferences[MERCHANT_ID] = id
        }
    }
    suspend fun saveOutletId(id:String){
        dataStore.edit { preferences ->
            preferences[OUTLET_ID] = id
        }
    }

    suspend fun setDeviceActivated(){
        dataStore.edit { preferences ->
            preferences[IS_DEVICE_ACTIVATED] = true
        }

    }
    suspend fun setLoggedIn(isLogged:Boolean){
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLogged
        }

    }

    suspend fun setAppMode(appMode: AppMode){
        dataStore.edit { preferences ->
            preferences[APP_MODE] = appMode.name
        }
    }
    suspend fun getAppMode(): AppMode {
        val name = dataStore.data.first()[APP_MODE] ?: AppMode.EzyCartPicker.name
        return AppMode.valueOf(name)
    }
    suspend fun clearPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun getAuthToken(): String? {
        return dataStore.data.first()[AUTH_TOKEN]
    }
    suspend fun getEmployeeName(): String {
        return dataStore.data.first()[EMPLOYEE_NAME] ?: ""
    }
    suspend fun getShoppingCartId(): String {
        return dataStore.data.first()[SHOPPING_CART_ID] ?: ""
    }

    suspend fun getXAuthToken(): String? {
        return dataStore.data.first()[X_AUTH_TOKEN]
    }
     fun getCartId(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[SHOPPING_CART_ID] ?: ""
        }.distinctUntilChanged()
    }
    suspend fun getMerchantId(): String {
        return dataStore.data.first()[MERCHANT_ID] ?: "11"
    }

    suspend fun getOutletId(): String {
        return dataStore.data.first()[OUTLET_ID] ?: "19"
    }

    fun isDeviceActivated(): Flow<Boolean?> {
        return dataStore.data.map { preferences ->
            preferences[IS_DEVICE_ACTIVATED]
        }.distinctUntilChanged()

    }

    suspend fun isLoggedIn(): Boolean {
        return dataStore.data.first()[IS_LOGGED_IN] ?: false

    }

    suspend fun clearEmployeeDetails() {
        dataStore.edit { preferences ->

            preferences.remove(EMPLOYEE_EMAIL)
            preferences.remove(X_AUTH_TOKEN)
            preferences.remove(EMPLOYEE_PIN)
            preferences.remove(EMPLOYEE_NAME)
            preferences.remove(SHOPPING_CART_ID)
            preferences.remove(AUTH_TOKEN)
        }
    }
}