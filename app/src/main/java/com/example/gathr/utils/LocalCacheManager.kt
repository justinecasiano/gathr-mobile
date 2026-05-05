import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gathr.presentation.main.UserCache
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "user_cache")

class LocalCacheManager(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }
    private val CACHE_KEY = stringPreferencesKey("user_state_cache")

    suspend fun saveCache(state: UserCache) {
        val serializedData = json.encodeToString(state)
        context.dataStore.edit { prefs ->
            prefs[CACHE_KEY] = serializedData
        }
    }

    suspend fun getCache(): UserCache? {
        val prefs = context.dataStore.data.first()
        val serializedData = prefs[CACHE_KEY] ?: return null
        return try {
            json.decodeFromString<UserCache>(serializedData)
        } catch (e: Exception) {
            null
        }
    }
}
