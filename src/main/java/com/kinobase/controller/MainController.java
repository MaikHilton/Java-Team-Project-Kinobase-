package com.kinobase.controller;

import com.kinobase.dao.GenreDao;
import com.kinobase.dao.CountryDao;
import com.kinobase.entity.Genre;
import com.kinobase.entity.Country;
import com.kinobase.entity.Movie;
import com.kinobase.entity.Rating;
import com.kinobase.entity.Viewer;
import com.kinobase.service.MovieService;
import com.kinobase.dao.ViewerDao;
import com.kinobase.dao.RatingDao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Головний контролер JavaFX.  Керує всіма вкладками: Фільми, Глядачі, Оцінки.
 *
 * @author Developer 2
 * @version 1.0
 */
public class MainController implements Initializable {

    // ─── Services / DAOs ─────────────────────────────────────────────────────
    private final MovieService  movieService  = new MovieService();
    private final ViewerDao     viewerDao     = new ViewerDao();
    private final RatingDao     ratingDao     = new RatingDao();
    private final GenreDao      genreDao      = new GenreDao();
    private final CountryDao    countryDao    = new CountryDao();

    // ══════════════════════════════════════════════════════════════════════════
    // ВКЛАДКА 1 – ФІЛЬМИ
    // ══════════════════════════════════════════════════════════════════════════
    @FXML private TableView<Movie>              movieTable;
    @FXML private TableColumn<Movie, Integer>   colMovieId;
    @FXML private TableColumn<Movie, String>    colMovieName;
    @FXML private TableColumn<Movie, String>    colGenre;
    @FXML private TableColumn<Movie, String>    colCountry;
    @FXML private TableColumn<Movie, Integer>   colYear;
    @FXML private TableColumn<Movie, String>    colRating;
    @FXML private TableColumn<Movie, Integer>   colVotes;

    // Форма фільму
    @FXML private TextField    tfMovieName;
    @FXML private ComboBox<String> cbGenre;
    @FXML private ComboBox<String> cbCountry;
    @FXML private TextField    tfYear;
    @FXML private Label        lblMovieMsg;

    // Пошук фільмів
    @FXML private ComboBox<String> cbSearchGenre;
    @FXML private ComboBox<String> cbSearchCountry;
    @FXML private TextField    tfSearchName;
    @FXML private TextField    tfYearFrom;
    @FXML private TextField    tfYearTo;

    // ══════════════════════════════════════════════════════════════════════════
    // ВКЛАДКА 2 – ГЛЯДАЧІ
    // ══════════════════════════════════════════════════════════════════════════
    @FXML private TableView<Viewer>             viewerTable;
    @FXML private TableColumn<Viewer, Integer>  colViewerId;
    @FXML private TableColumn<Viewer, String>   colViewerName;
    @FXML private TextField    tfViewerName;
    @FXML private Label        lblViewerMsg;

    // ══════════════════════════════════════════════════════════════════════════
    // ВКЛАДКА 3 – ОЦІНКИ
    // ══════════════════════════════════════════════════════════════════════════
    @FXML private TableView<Rating>             ratingTable;
    @FXML private TableColumn<Rating, String>   colRatingViewer;
    @FXML private TableColumn<Rating, String>   colRatingMovie;
    @FXML private TableColumn<Rating, Integer>  colRatingScore;

    @FXML private ComboBox<Viewer> cbRatingViewer;
    @FXML private ComboBox<Movie>  cbRatingMovie;
    @FXML private Spinner<Integer> spRatingScore;
    @FXML private Label            lblRatingMsg;

    // ─── Ініціалізація ───────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupMovieTable();
        setupViewerTable();
        setupRatingTable();
        loadComboBoxes();
        refreshMovies();
        refreshViewers();
        refreshRatings();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // НАЛАШТУВАННЯ ТАБЛИЦЬ
    // ══════════════════════════════════════════════════════════════════════════

    private void setupMovieTable() {
        colMovieId  .setCellValueFactory(new PropertyValueFactory<>("movieId"));
        colMovieName.setCellValueFactory(new PropertyValueFactory<>("movieName"));
        colGenre    .setCellValueFactory(new PropertyValueFactory<>("genre"));
        colCountry  .setCellValueFactory(new PropertyValueFactory<>("country"));
        colYear     .setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        colVotes    .setCellValueFactory(new PropertyValueFactory<>("voteCount"));
        colRating   .setCellValueFactory(cd -> {
            BigDecimal avg = cd.getValue().getAvgRating();
            int votes = cd.getValue().getVoteCount();
            String text = (votes > 10 && avg != null)
                    ? avg.toPlainString() + " ⭐"
                    : "–  (< 10 голосів)";
            return new SimpleStringProperty(text);
        });
        movieTable.setPlaceholder(new Label("Фільмів не знайдено"));
    }

    private void setupViewerTable() {
        colViewerId  .setCellValueFactory(new PropertyValueFactory<>("viewerId"));
        colViewerName.setCellValueFactory(new PropertyValueFactory<>("name"));
        viewerTable.setPlaceholder(new Label("Глядачів не знайдено"));
    }

    private void setupRatingTable() {
        colRatingViewer.setCellValueFactory(new PropertyValueFactory<>("viewerName"));
        colRatingMovie .setCellValueFactory(new PropertyValueFactory<>("movieName"));
        colRatingScore .setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingTable.setPlaceholder(new Label("Оцінок не знайдено"));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ЗАВАНТАЖЕННЯ КОМБОБОКСІВ
    // ══════════════════════════════════════════════════════════════════════════

    private void loadComboBoxes() {
        try {
            List<String> genres    = genreDao.findAll().stream().map(Genre::getGenreName).toList();
            List<String> countries = countryDao.findAll().stream().map(Country::getCountryName).toList();

            cbGenre  .setItems(FXCollections.observableArrayList(genres));
            cbCountry.setItems(FXCollections.observableArrayList(countries));
            cbSearchGenre  .setItems(FXCollections.observableArrayList(genres));
            cbSearchCountry.setItems(FXCollections.observableArrayList(countries));

            cbRatingViewer.setItems(FXCollections.observableArrayList(viewerDao.findAll()));
            cbRatingMovie .setItems(FXCollections.observableArrayList(movieService.getAllMovies()));

            spRatingScore.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 5));
        } catch (SQLException e) {
            showError(lblMovieMsg, "Помилка завантаження довідників: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ФІЛЬМИ – дії
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void onAddMovie() {
        try {
            Movie m = buildMovieFromForm();
            movieService.addMovie(m);
            refreshMovies();
            clearMovieForm();
            showSuccess(lblMovieMsg, "Фільм «" + m.getMovieName() + "» додано!");
        } catch (Exception e) {
            showError(lblMovieMsg, e.getMessage());
        }
    }

    @FXML
    private void onUpdateMovie() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError(lblMovieMsg, "Оберіть фільм у таблиці"); return; }
        try {
            Movie m = buildMovieFromForm();
            m.setMovieId(selected.getMovieId());
            movieService.updateMovie(m);
            refreshMovies();
            clearMovieForm();
            showSuccess(lblMovieMsg, "Фільм оновлено!");
        } catch (Exception e) {
            showError(lblMovieMsg, e.getMessage());
        }
    }

    @FXML
    private void onDeleteMovie() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError(lblMovieMsg, "Оберіть фільм у таблиці"); return; }
        try {
            movieService.deleteMovie(selected.getMovieId());
            refreshMovies();
            showSuccess(lblMovieMsg, "Фільм видалено!");
        } catch (SQLException e) {
            showError(lblMovieMsg, "Помилка видалення: " + e.getMessage());
        }
    }

    /** Заповнити форму даними виділеного рядка. */
    @FXML
    private void onSelectMovie() {
        Movie m = movieTable.getSelectionModel().getSelectedItem();
        if (m == null) return;
        tfMovieName.setText(m.getMovieName());
        cbGenre    .setValue(m.getGenre());
        cbCountry  .setValue(m.getCountry());
        tfYear     .setText(String.valueOf(m.getReleaseYear()));
    }

    // ─── Пошук ───────────────────────────────────────────────────────────────

    @FXML
    private void onSearchMovies() {
        try {
            String genre   = cbSearchGenre.getValue();
            String country = cbSearchCountry.getValue();
            String name    = tfSearchName.getText().trim();
            String fromStr = tfYearFrom.getText().trim();
            String toStr   = tfYearTo.getText().trim();

            List<Movie> result;

            if (!name.isEmpty()) {
                result = movieService.searchByName(name);
            } else if (genre != null && country != null) {
                result = movieService.searchByGenreAndCountry(genre, country);
            } else if (genre != null) {
                result = movieService.searchByGenre(genre);
            } else if (country != null) {
                result = movieService.searchByCountry(country);
            } else if (!fromStr.isEmpty() && !toStr.isEmpty()) {
                result = movieService.searchByYearRange(Integer.parseInt(fromStr), Integer.parseInt(toStr));
            } else {
                result = movieService.getAllMovies();
            }

            movieTable.setItems(FXCollections.observableArrayList(result));
            lblMovieMsg.setText("Знайдено: " + result.size());
        } catch (Exception e) {
            showError(lblMovieMsg, "Помилка пошуку: " + e.getMessage());
        }
    }

    @FXML
    private void onResetSearch() {
        cbSearchGenre.setValue(null);
        cbSearchCountry.setValue(null);
        tfSearchName.clear();
        tfYearFrom.clear();
        tfYearTo.clear();
        refreshMovies();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ГЛЯДАЧІ – дії
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void onAddViewer() {
        String name = tfViewerName.getText().trim();
        if (name.isEmpty()) { showError(lblViewerMsg, "Введіть ім'я глядача"); return; }
        try {
            Viewer v = new Viewer(name);
            viewerDao.insert(v);
            refreshViewers();
            tfViewerName.clear();
            showSuccess(lblViewerMsg, "Глядача «" + name + "» додано!");
            loadComboBoxes();
        } catch (SQLException e) {
            showError(lblViewerMsg, "Помилка: " + e.getMessage());
        }
    }

    @FXML
    private void onUpdateViewer() {
        Viewer selected = viewerTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError(lblViewerMsg, "Оберіть глядача у таблиці"); return; }
        String name = tfViewerName.getText().trim();
        if (name.isEmpty()) { showError(lblViewerMsg, "Введіть нове ім'я"); return; }
        try {
            selected.setName(name);
            viewerDao.update(selected);
            refreshViewers();
            showSuccess(lblViewerMsg, "Глядача оновлено!");
        } catch (SQLException e) {
            showError(lblViewerMsg, "Помилка: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteViewer() {
        Viewer selected = viewerTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError(lblViewerMsg, "Оберіть глядача"); return; }
        try {
            viewerDao.delete(selected.getViewerId());
            refreshViewers();
            loadComboBoxes();
            showSuccess(lblViewerMsg, "Глядача видалено!");
        } catch (SQLException e) {
            showError(lblViewerMsg, "Помилка: " + e.getMessage());
        }
    }

    @FXML
    private void onSelectViewer() {
        Viewer v = viewerTable.getSelectionModel().getSelectedItem();
        if (v != null) tfViewerName.setText(v.getName());
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ОЦІНКИ – дії
    // ══════════════════════════════════════════════════════════════════════════

    @FXML
    private void onAddRating() {
        Viewer viewer = cbRatingViewer.getValue();
        Movie  movie  = cbRatingMovie.getValue();
        if (viewer == null || movie == null) {
            showError(lblRatingMsg, "Оберіть глядача та фільм"); return;
        }
        try {
            int score = spRatingScore.getValue();
            movieService.rateMovie(viewer.getViewerId(), movie.getMovieId(), score);
            refreshRatings();
            refreshMovies();
            showSuccess(lblRatingMsg, "Оцінку збережено!");
        } catch (Exception e) {
            showError(lblRatingMsg, "Помилка: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteRating() {
        Rating selected = ratingTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError(lblRatingMsg, "Оберіть оцінку"); return; }
        try {
            ratingDao.delete(selected.getViewerId(), selected.getMovieId());
            refreshRatings();
            refreshMovies();
            showSuccess(lblRatingMsg, "Оцінку видалено!");
        } catch (SQLException e) {
            showError(lblRatingMsg, "Помилка: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // REFRESH
    // ══════════════════════════════════════════════════════════════════════════

    private void refreshMovies() {
        try {
            movieTable.setItems(FXCollections.observableArrayList(movieService.getAllMovies()));
        } catch (SQLException e) {
            showError(lblMovieMsg, "Помилка завантаження: " + e.getMessage());
        }
    }

    private void refreshViewers() {
        try {
            viewerTable.setItems(FXCollections.observableArrayList(viewerDao.findAll()));
        } catch (SQLException e) {
            showError(lblViewerMsg, "Помилка завантаження: " + e.getMessage());
        }
    }

    private void refreshRatings() {
        try {
            ratingTable.setItems(FXCollections.observableArrayList(ratingDao.findAll()));
        } catch (SQLException e) {
            showError(lblRatingMsg, "Помилка завантаження: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ══════════════════════════════════════════════════════════════════════════

    private Movie buildMovieFromForm() {
        String name = tfMovieName.getText().trim();
        String genre   = cbGenre.getValue();
        String country = cbCountry.getValue();
        String yearStr = tfYear.getText().trim();
        if (name.isEmpty())   throw new IllegalArgumentException("Введіть назву фільму");
        if (yearStr.isEmpty()) throw new IllegalArgumentException("Введіть рік виходу");
        int year;
        try { year = Integer.parseInt(yearStr); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Рік має бути числом"); }
        return new Movie(name, genre, country, year);
    }

    private void clearMovieForm() {
        tfMovieName.clear();
        cbGenre.setValue(null);
        cbCountry.setValue(null);
        tfYear.clear();
    }

    private void showSuccess(Label lbl, String msg) {
        lbl.setText("✅ " + msg);
        lbl.setStyle("-fx-text-fill: #27ae60;");
    }

    private void showError(Label lbl, String msg) {
        if (lbl == null) { System.err.println(msg); return; }
        lbl.setText("❌ " + msg);
        lbl.setStyle("-fx-text-fill: #e74c3c;");
    }
}
