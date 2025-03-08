package nz.co.test.transactions.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import nz.co.test.transactions.data.FakeTransactionRepository
import nz.co.test.transactions.data.TransactionRepository
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class]
)
object RepositoryTestModule {

    @Singleton
    @Provides
    fun provideTransactionsRepository(): TransactionRepository {
        return FakeTransactionRepository()
    }
}
