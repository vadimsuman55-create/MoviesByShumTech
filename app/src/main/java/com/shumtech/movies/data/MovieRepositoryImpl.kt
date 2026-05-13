package com.shumtech.movies.data

import com.shumtech.movies.domain.MovieRepository
import com.shumtech.movies.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepositoryImpl(
    private val movieDao: MovieDao,
    private val api: MovieApi,
    private val tmdbApi: TmdbApiService
) : MovieRepository {

    override fun getAllMovies(): Flow<List<Movie>> = movieDao.getAllMovies()

    override suspend fun insertMovie(movie: Movie) {
        movieDao.insertMovie(movie.copy(isSelected = false))
    }

    override suspend fun updateMovie(movie: Movie) {
        movieDao.updateMovie(movie)
    }

    override suspend fun deleteSelectedMovies() {
        movieDao.deleteSelectedMovies()
    }

    override suspend fun getMovieById(id: Int): Movie? = movieDao.getMovieById(id)

    override suspend fun searchMovies(query: String): List<Movie> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchMovies(RetrofitClient.API_KEY, query)
            if (response.Response == "True" && response.Search != null) {
                val genres = listOf("Драма", "Комедия", "Боевик", "Триллер", "Фантастика", "Ужасы", "Мелодрама")
                response.Search.mapIndexed { index, result ->
                    Movie(
                        title = result.Title,
                        year = result.Year,
                        posterUrl = result.Poster,
                        imdbID = result.imdbID,
                        genre = genres[index % genres.size],
                        isSelected = false
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getMovieTrailer(tmdbId: Int): String? = withContext(Dispatchers.IO) {
        try {
            val response = tmdbApi.getMovieVideos(
                movieId = tmdbId,
                apiKey = BuildConfig.TMDB_API_KEY,
                language = "ru-RU"
            )

            // Ищем официальный трейлер на YouTube
            response.results
                .filter { it.site == "YouTube" && it.type == "Trailer" }
                .firstOrNull { it.official }?.key
                ?: response.results.firstOrNull { it.site == "YouTube" }?.key
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}