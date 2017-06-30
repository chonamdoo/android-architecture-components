package com.android.example.github.ui.search;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.example.github.vo.Repo;
import java.util.Collections;
import java.util.List;

/**
 * An immutable model that represents the state of the business logic.
 * This model can be rendered directly in the View
 *
 * It has different states:
 *
 * - isSearchNotStarted: Means, user has not inserted any search query yet. This is typically the
 * case when the apps starts.
 * - firstPageLoading or firstPageError: Indicates that the page is loading for the first time OR an
 * error has occured while loading the first time
 * - nextPageLoading or nextPage error: indicates that the next page is loading (or an error has
 * occurred while loading the next page)
 *
 * results holds a list of all data that has been loaded
 *
 * @author Hannes Dorfmann
 */
public final class SearchState {

  @NonNull private String query;
  private boolean firstPageLoading;
  @Nullable private String firstPageError;
  @NonNull private List<Repo> results;
  private boolean nextPageLoading;
  @Nullable private String nextPageError;
  private boolean searchNotStarted;

  private SearchState(@NonNull String query, boolean firstPageLoading, String firstPageError,
      @NonNull List<Repo> results, boolean nextPageLoading, String nextPageError,
      boolean searchNotStarted) {
    this.query = query;
    this.firstPageLoading = firstPageLoading;
    this.firstPageError = firstPageError;
    this.results = results;
    this.nextPageLoading = nextPageLoading;
    this.nextPageError = nextPageError;
    this.searchNotStarted = searchNotStarted;
  }

  public boolean isSearchNotStarted() {
    return this.searchNotStarted;
  }

  @NonNull public String getQuery() {
    return query;
  }

  public boolean isFirstPageLoading() {
    return firstPageLoading;
  }

  @Nullable public String getFirstPageError() {
    return firstPageError;
  }

  @NonNull public List<Repo> getResults() {
    return results;
  }

  public boolean isNextPageLoading() {
    return nextPageLoading;
  }

  @Nullable public String getNextPageError() {
    return nextPageError;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SearchState that = (SearchState) o;

    if (firstPageLoading != that.firstPageLoading) return false;
    if (nextPageLoading != that.nextPageLoading) return false;
    if (searchNotStarted != that.searchNotStarted) return false;
    if (!query.equals(that.query)) return false;
    if (firstPageError != null ? !firstPageError.equals(that.firstPageError)
        : that.firstPageError != null) {
      return false;
    }
    if (!results.equals(that.results)) return false;
    return nextPageError != null ? nextPageError.equals(that.nextPageError)
        : that.nextPageError == null;
  }

  @Override public int hashCode() {
    int result = query.hashCode();
    result = 31 * result + (firstPageLoading ? 1 : 0);
    result = 31 * result + (firstPageError != null ? firstPageError.hashCode() : 0);
    result = 31 * result + results.hashCode();
    result = 31 * result + (nextPageLoading ? 1 : 0);
    result = 31 * result + (nextPageError != null ? nextPageError.hashCode() : 0);
    result = 31 * result + (searchNotStarted ? 1 : 0);
    return result;
  }

  @Override public String toString() {
    return "SearchState{"
        + "query='"
        + query
        + '\''
        + ", firstPageLoading="
        + firstPageLoading
        + ", firstPageError='"
        + firstPageError
        + '\''
        + ", nextPageLoading="
        + nextPageLoading
        + ", nextPageError='"
        + nextPageError
        + '\''
        + ", searchNotStarted="
        + searchNotStarted
        + ", results="
        + results
        + '}';
  }

  /**
   * A builder instance prefilled with this model.
   * This is the only way to change the model class (by creating a new immutable copy)
   *
   * @return The builder
   */
  public Builder builder() {
    return new Builder(this);
  }

  public static final class Builder {

    @NonNull private String query;
    private boolean firstPageLoading;
    @Nullable private String firstPageError;
    @NonNull private List<Repo> results;
    private boolean nextPageLoading;
    @Nullable private String nextPageError;
    private boolean searchNotStated;

    public Builder() {
    }

    private Builder(SearchState model) {
      this.query = model.query;
      this.firstPageLoading = model.firstPageLoading;
      this.firstPageError = model.firstPageError;
      this.results = model.results;
      this.nextPageLoading = model.nextPageLoading;
      this.nextPageError = model.nextPageError;
      this.searchNotStated = model.searchNotStarted;
    }

    public Builder query(@NonNull String query) {
      if (query == null) {
        throw new NullPointerException("query == null");
      }
      this.query = query;
      return this;
    }

    public Builder firstPageLoading(boolean firstPageLoading) {
      this.firstPageLoading = firstPageLoading;
      return this;
    }

    public Builder firstPageError(@Nullable String error) {
      this.firstPageError = error;
      return this;
    }

    public Builder results(@NonNull List<Repo> results) {
      if (results == null) {
        throw new NullPointerException("resutls == null");
      }
      this.results = results;
      return this;
    }

    public Builder nextPageLoading(boolean nextPageLoading) {
      this.nextPageLoading = nextPageLoading;
      return this;
    }

    public Builder nextPageError(@Nullable String nextPageError) {
      this.nextPageError = nextPageError;
      return this;
    }

    public Builder searchNotStarted(boolean searchNotStated) {
      this.searchNotStated = searchNotStated;
      return this;
    }

    public SearchState build() {

      if (query == null) {
        throw new NullPointerException("query == null");
      }

      if (results == null) {
        results = Collections.emptyList();
      }

      return new SearchState(query, firstPageLoading, firstPageError, results, nextPageLoading,
          nextPageError, searchNotStated);
    }
  }
}
