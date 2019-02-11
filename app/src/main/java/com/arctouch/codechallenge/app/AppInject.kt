package com.arctouch.codechallenge.app

import com.arctouch.codechallenge.data.api.RestClient
import com.arctouch.codechallenge.data.repository.MoviesRepository
import com.arctouch.codechallenge.ui.base.BaseViewModel
import com.arctouch.codechallenge.ui.details.DetailsViewModel
import com.arctouch.codechallenge.ui.home.HomeViewModel
import io.coroutines.cache.core.CoroutinesCache
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module

object AppInject {

    fun modules() : List<Module> = listOf(
            applicationModule,
            viewModelModule,
            repositoriesModule
    )

    private val applicationModule: Module = module {
        single { RestClient().api }
        single { CoroutinesCache(get())}
    }

    private val viewModelModule = module {
        viewModel{ BaseViewModel()}
        viewModel{ HomeViewModel(get())}
        viewModel{ DetailsViewModel(get())}
    }

    private val repositoriesModule: Module = module {
        single{ MoviesRepository(get(), get())}
    }
}