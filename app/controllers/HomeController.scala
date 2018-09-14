package controllers
import play.api.libs.json._
import models._
import models.PlaceFormats._
import javax.inject._
import play.api._
import play.api.mvc._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents, placerep : PlaceRepository) extends AbstractController(cc) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.place())
  }
  def near(long : Double, lat : Double, rad : Double) = Action.async {
    placerep.near(long, lat, rad).map { places =>
      Ok(Json.toJson(places))
    }
  }

  def place() = Action.async { 
    placerep.getAll().map { places =>
      Ok(Json.toJson(places))
    }
  }

  def getOne(_id : String) = Action { implicit request: Request[AnyContent] => 
    val one = placerep.getOne(_id)
    Ok(Json.toJson(one))
  }

}

