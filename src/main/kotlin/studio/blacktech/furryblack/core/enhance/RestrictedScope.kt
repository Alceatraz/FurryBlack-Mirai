package studio.blacktech.furryblack.core.enhance

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class RestrictedScope(override val coroutineContext: CoroutineContext = Job()) : CoroutineScope