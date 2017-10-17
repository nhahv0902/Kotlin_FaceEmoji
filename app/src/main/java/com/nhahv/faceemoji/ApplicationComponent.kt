package com.nhahv.faceemoji

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector

/**
 * Created by nhahv on 10/18/17.
 */
@Component(modules = arrayOf(AndroidInjectionModule::class, ApplicationModule::class))
interface ApplicationComponent : AndroidInjector<AppApplication> {

}
