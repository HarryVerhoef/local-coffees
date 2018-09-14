package models

import play.api.libs.json.Json
import scala.concurrent.{ExecutionContext, Future}
import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.{BSONArray, BSONDocument, BSONDouble, BSONObjectID}
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._
import play.api.libs.json._

case class Location (`type`: String, coordinates: Array[Double])

case class Place (_id : Option[BSONObjectID], name : String, email : String, location : Location)

object PlaceFormats {
    import play.api.libs.json._
    implicit val locationFormat = Json.format[Location]
    implicit val placeFormat = Json.format[Place]
}

class PlaceRepository @Inject()(implicit ec: ExecutionContext, reactiveMongoApi: ReactiveMongoApi) {

    import PlaceFormats._

    def placesCollection: Future[JSONCollection] = reactiveMongoApi.database.map(_.collection("place"))

    def getOne(_id : String) : Place = {
        // BSONObjectID(_id)
        Place(None,"tarbucks","info@tarbucks.net",Location("Point",Array(10.0,10.0)))
    }

    def getAll() : Future[Seq[Place]] =
        placesCollection.flatMap(_.find(
        selector = Json.obj(),
        projection = Option.empty[JsObject])
        .cursor[Place](ReadPreference.primary)
        .collect[Seq](100, Cursor.FailOnError[Seq[Place]]())
    )
    
    def near(long : Double, lat : Double, rad : Double) : Future[Seq[Place]] =
        
        placesCollection.flatMap(_.find(
        selector = Json.obj("location" -> Json.obj("$geoWithin" -> Json.obj("$centerSphere" -> Json.arr(Json.arr(lat,long),rad * 0.000568182/ 3963.2)))),
        projection = Option.empty[JsObject])
        .cursor[Place](ReadPreference.primary)
        .collect[Seq](100, Cursor.FailOnError[Seq[Place]]()))
}