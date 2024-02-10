package com.irancell.nwg.ios.presentation.audit.viewModel

import androidx.lifecycle.ViewModel
import com.irancell.nwg.ios.repository.AuditFormRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContainerFragmentViewModel @Inject constructor(
    private val auditFormRepository: AuditFormRepository
) : ViewModel() {
}