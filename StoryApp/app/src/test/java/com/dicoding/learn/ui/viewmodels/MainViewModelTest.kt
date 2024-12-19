package com.dicoding.learn.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.learn.data.model.ListStoryItem
import com.dicoding.learn.data.repository.AuthRepository
import com.dicoding.learn.data.repository.StoryRepository
import com.dicoding.learn.helper.DataDummy
import com.dicoding.learn.helper.MainDispatcherRule
import com.dicoding.learn.helper.getOrAwaitValue
import com.dicoding.learn.ui.adapter.StoryListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Mock
    private lateinit var authRepository: AuthRepository

    @Test
    fun `when get Stories Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyStoryResponse()
        val data : PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStory)
        val expectData = MutableLiveData<PagingData<ListStoryItem>>()
        expectData.value = data

        Mockito.`when`(storyRepository.getAllStory()).thenReturn(expectData)


        val mainViewModel = MainViewModel(storyRepository, authRepository)
        val actualStory : PagingData<ListStoryItem> = mainViewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStory)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStory.size, differ.snapshot().size)
        Assert.assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `when Get Quote Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
        expectedQuote.value = data
        Mockito.`when`(storyRepository.getAllStory()).thenReturn(expectedQuote)
        val mainViewModel = MainViewModel(storyRepository, authRepository)
        val actualQuote: PagingData<ListStoryItem> = mainViewModel.story.getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryListAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualQuote)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback{
    override fun onInserted(position: Int, count: Int) {
    }

    override fun onRemoved(position: Int, count: Int) {
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
    }

}

class StoryPagingSource : PagingSource<Int, ListStoryItem>() {
    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return LoadResult.Page(emptyList(), prevKey = 0, nextKey = 1)
    }

    companion object {
        fun snapshot(items : List<ListStoryItem>) : PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
}