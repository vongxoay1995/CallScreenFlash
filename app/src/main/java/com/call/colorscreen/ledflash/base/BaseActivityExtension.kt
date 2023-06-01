import com.call.colorscreen.ledflash.base.BaseActivity

fun BaseActivity<*>?.isActive(): Boolean {
    return this != null && hasActive()
}