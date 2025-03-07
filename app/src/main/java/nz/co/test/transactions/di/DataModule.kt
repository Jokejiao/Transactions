package nz.co.test.transactions.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import nz.co.test.transactions.data.source.local.TransactionDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TransactionDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TransactionDatabase::class.java,
            "Transactions.db"
        ).build()
    }

    @Provides
    fun provideTransactionDao(database: TransactionDatabase) = database.transactionDao()
}