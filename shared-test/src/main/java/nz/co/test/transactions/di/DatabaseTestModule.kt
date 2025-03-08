package nz.co.test.transactions.di

import android.content.Context
import androidx.room.Room
import nz.co.test.transactions.data.source.local.TransactionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object DatabaseTestModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): TransactionDatabase {
        return Room
            .inMemoryDatabaseBuilder(context.applicationContext, TransactionDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }
}
