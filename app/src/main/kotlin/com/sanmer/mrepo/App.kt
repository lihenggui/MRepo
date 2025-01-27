package com.sanmer.mrepo

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.sanmer.mrepo.app.isFailed
import com.sanmer.mrepo.app.isNon
import com.sanmer.mrepo.app.isSucceeded
import com.sanmer.mrepo.app.utils.NotificationUtils
import com.sanmer.mrepo.di.MainScope
import com.sanmer.mrepo.provider.SuProviderImpl
import com.sanmer.mrepo.repository.UserPreferencesRepository
import com.sanmer.mrepo.utils.timber.DebugTree
import com.sanmer.mrepo.utils.timber.ReleaseTree
import com.sanmer.mrepo.works.LocalWork
import com.sanmer.mrepo.works.RepoWork
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var userPreferences: UserPreferencesRepository

    @Inject
    lateinit var suProviderImpl: SuProviderImpl

    @MainScope
    @Inject
    lateinit var mainScope: CoroutineScope

    private val workManger by lazy { WorkManager.getInstance(this) }

    init {
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }
    }

    override fun onCreate() {
        super.onCreate()

        NotificationUtils.init(this)

        initSuProviderImpl()
        workManger.enqueue(RepoWork.OneTimeWork)
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun initSuProviderImpl() {
        userPreferences.data
            .map { it.isRoot }
            .distinctUntilChanged()
            .combine(suProviderImpl.state) { isRoot, state ->
                when {
                    state.isNon && isRoot -> suProviderImpl.init()
                    state.isSucceeded -> workManger.enqueue(LocalWork.OneTimeWork)
                    state.isFailed -> {
                        delay(15000)
                        suProviderImpl.init()
                    }
                    else -> {}
                }
            }.launchIn(mainScope)
    }
}