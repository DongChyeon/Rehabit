package com.co77iri.imu_walking_pattern.network.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.co77iri.imu_walking_pattern.network.adapter.ApiResult
import com.co77iri.imu_walking_pattern.network.models.response.ParkinsonTestData
import com.co77iri.imu_walking_pattern.network.repository.PatientRepository
import kotlinx.coroutines.flow.first

class ParkinsonTestDataPagingSource(
    private val patientRepository: PatientRepository,
    private val patientId: Int,
    private val pageSize: Int = 10
) : PagingSource<Int, ParkinsonTestData>() {

    override fun getRefreshKey(state: PagingState<Int, ParkinsonTestData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ParkinsonTestData> {
        val currentPage = params.key ?: 0

        val loadData =
            patientRepository.getParkinsonTestDataList(
                patientId = patientId,
                pageNumber = currentPage,
                pageSize = pageSize
            ).first()
        return when (loadData) {
            is ApiResult.Success -> {
                try {
                    val result = loadData.data.result

                    LoadResult.Page(
                        data = result.content,
                        prevKey = if (result.first) null else currentPage - 1,
                        nextKey = if (result.last) null else currentPage + 1
                    )
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }

            is ApiResult.ApiError -> {
                LoadResult.Error(Exception(loadData.message))
            }

            is ApiResult.NetworkError -> {
                LoadResult.Error(Exception("네트워크 오류가 발생했습니다."))
            }
        }
    }

}