package com.gtech.gossipmessenger.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.gtech.gossipmessenger.utils.Resource.Status.ERROR
import com.gtech.gossipmessenger.utils.Resource.Status.SUCCESS
import kotlinx.coroutines.Dispatchers

/**
 * Perform databaseQuery, networkCalls
 */
fun <T, A> performFullOperation(
    databaseQuery: () -> LiveData<T>,
    networkCall: suspend () -> Resource<A>,
    saveCallResult: suspend (A) -> Unit
): LiveData<Resource<T>> = liveData(Dispatchers.IO) {
    emit(Resource.loading())
    val source = databaseQuery.invoke().map { Resource.success(it) }
    emitSource(source)

    val responseStatus = networkCall.invoke()
    if (responseStatus.status == SUCCESS) {
        saveCallResult(responseStatus.data!!)
    } else if (responseStatus.status == ERROR) {
        emit(Resource.error(responseStatus.message!!))
        emitSource(source)
    }
}

/**
 * Perform network calls
 */
fun <T> performNetworkOperation(
    networkCall: suspend () -> Resource<T>
): LiveData<Resource<T>> = liveData(Dispatchers.IO) {
    emit(Resource.loading())

    val responseStatus = networkCall.invoke()
    if (responseStatus.status == SUCCESS) {
        emit(Resource.success(responseStatus.data!!))
    } else if (responseStatus.status == ERROR) {
        emit(Resource.error(responseStatus.message!!))
    }
}

fun <T> performLocalOperation(
    databaseQuery: () -> LiveData<T>
): LiveData<Resource<T>> = liveData(Dispatchers.IO) {
    emit(Resource.loading())

    val source = databaseQuery.invoke().map { Resource.success(it) }
    emitSource(source)
}
