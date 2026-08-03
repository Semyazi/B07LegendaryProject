package com.professional.b07legendaryproject2026.fragments;

import androidx.fragment.app.Fragment;

public class BackBtnBaseFragment extends Fragment {

    protected void navigateBack() {
        if (requireActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
            requireActivity().getSupportFragmentManager().popBackStack();
        }

        else {
            requireActivity().finish();
        }
    }
}