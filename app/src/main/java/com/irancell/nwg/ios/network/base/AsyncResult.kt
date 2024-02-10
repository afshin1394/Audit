import com.irancell.nwg.ios.network.base.AsyncStatus

sealed class AsyncResult <out T> (val status: AsyncStatus, val data: T?, val message:String?,val code: String?) {

    data class Success<out R>(val _data: R?,val _code : String): AsyncResult<R>(
        status = AsyncStatus.SUCCESS,
        data = _data,
        message = null,
        code = _code

    )

    data class Error(val exception: String,val _code: String): AsyncResult<Nothing>(
        status = AsyncStatus.ERROR,
        data = null,
        message = exception,
        code = _code

    )

    data class Loading<out R>(val _data: R?, val isLoading: Boolean): AsyncResult<R>(
        status = AsyncStatus.LOADING,
        data = _data,
        message = null,
        code = null
    )
}