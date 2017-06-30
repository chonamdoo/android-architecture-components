/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.github.ui.search;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;
import com.android.example.github.repository.RepoRepository;
import com.android.example.github.vo.Resource;
import com.android.example.github.vo.Status;
import javax.inject.Inject;

public class SearchViewModel extends ViewModel {

  private final RepoRepository repoRepository;

  private final SearchState INITIAL_STATE =
      new SearchState.Builder().query("").searchNotStarted(true).build();

  private MutableLiveData<SearchState> stateLiveDate = new MutableLiveData<>();

  // Some workaround, maybe there is a better way
  private LiveData<SearchState> firstPageLiveData = null;
  private Observer<SearchState> firstPageObserver = new Observer<SearchState>() {
    @Override public void onChanged(@Nullable SearchState searchState) {
      if (lastState().getQuery().equals(searchState.getQuery())) {
        publishState(searchState);
      }
    }
  };

  private LiveData<Resource<Boolean>> nextPageLiveData = null;
  private Observer<Resource<Boolean>> nextPageObserver = new Observer<Resource<Boolean>>() {
    @Override public void onChanged(@Nullable Resource<Boolean> loadNextResource) {
      if (loadNextResource != null) {
        switch (loadNextResource.status) {
          case SUCCESS: {
            SearchState updatedState =
                lastState().builder().nextPageLoading(false).nextPageError(null).build();

            publishState(updatedState);
            break;
          }

          case ERROR: {
            SearchState updatedState = lastState().builder()
                .nextPageLoading(false)
                .nextPageError(loadNextResource.message)
                .build();
            publishState(updatedState);
          }
        }
      }
    }
  };

  @Inject SearchViewModel(RepoRepository repoRepository) {
    this.repoRepository = repoRepository;
  }

  public LiveData<SearchState> state() {
    return stateLiveDate;
  }

  public void search(String query) {

    if (firstPageLiveData != null) {
      // Some workaround: Similar to switchMap
      firstPageLiveData.removeObserver(firstPageObserver);
    }

    if (query == null || query.length() == 0) {
      publishState(INITIAL_STATE);
      return;
    }

    // Show loading indicator
    publishState(new SearchState.Builder().query(query)
        .searchNotStarted(false)
        .firstPageLoading(true)
        .build());

    firstPageLiveData = Transformations.map(repoRepository.search(query), resource -> {
      if (resource.status == Status.SUCCESS) {
        return lastState().builder()
            .searchNotStarted(false)
            .firstPageLoading(false)
            .results(resource.data)
            .nextPageError(null)
            .nextPageLoading(false)
            .build();
      } else {
        return lastState().builder()
            .searchNotStarted(false)
            .firstPageLoading(false)
            .firstPageError(resource.message)
            .nextPageError(null)
            .nextPageLoading(false)
            .build();
      }
    });

    firstPageLiveData.observeForever(firstPageObserver);
  }

  public void nextPage() {
    if (nextPageLiveData != null) {
      nextPageLiveData.removeObserver(nextPageObserver);
    }
    publishState(lastState().builder().nextPageLoading(true).nextPageError(null).build());
    nextPageLiveData = repoRepository.searchNextPage(lastState().getQuery());
    nextPageLiveData.observeForever(nextPageObserver);
  }

  public void retrySearch(){
    // Simple solution
    search(lastState().getQuery());
  }

  public void clearNextPageErrorMessage() {
    publishState(lastState().builder().nextPageError(null).build());
  }


  @Override protected void onCleared() {
    super.onCleared();

    if (firstPageLiveData != null) {
      firstPageLiveData.removeObserver(firstPageObserver);
    }

    if (nextPageLiveData != null) {
      nextPageLiveData.removeObserver(nextPageObserver);
    }
  }

  private SearchState lastState() {
    return stateLiveDate.getValue();
  }

  private void publishState(SearchState newState) {
    stateLiveDate.setValue(newState);
  }
}
