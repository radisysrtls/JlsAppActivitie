//
//  MapsScreen.swift
//  PeopleTracker
//
/*************************************************************
 *
 * Reliance Digital Platform & Product Services Ltd.
 * CONFIDENTIAL
 * __________________
 *
 *  Copyright (C) 2020 Reliance Digital Platform & Product Services Ltd.–
 *
 *  ALL RIGHTS RESERVED.
 *
 * NOTICE:  All information including computer software along with source code and associated *documentation contained herein is, and
 * remains the property of Reliance Digital Platform & Product Services Ltd..  The
 * intellectual and technical concepts contained herein are
 * proprietary to Reliance Digital Platform & Product Services Ltd. and are protected by
 * copyright law or as trade secret under confidentiality obligations.
 * Dissemination, storage, transmission or reproduction of this information
 * in any part or full is strictly forbidden unless prior written
 * permission along with agreement for any usage right is obtained from Reliance Digital Platform & *Product Services Ltd.
 **************************************************************/


import UIKit
import MapKit
import GoogleMaps

class MapsScreen: UIViewController {

    @IBOutlet weak var mapView: MKMapView!
    
    let googleApiKey = "AIzaSyCL18AjsFlIRWkG5_BcHEZsOnFDE0aok2w"
    let regionRadius: CLLocationDistance = 1000
    var names : [String] = []
    var latitude : [Double] = [-33.86, -39.86]
    var longitude : [Double] = [151.20, 155.20]
    var deviceId : String = ""
    var userId : String = ""
    var ugsToken : String = ""
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.title = "Location"
        self.callgetDeviceLocationDetails()
        // Maps Key
        GMSServices.provideAPIKey(googleApiKey)
        
        createmapView()
       
    }

    // create mapview to display maps
    func createmapView() {
        let camera = GMSCameraPosition.camera(withLatitude: -33.86, longitude: 151.20, zoom: 6.0)
        let mapView = GMSMapView.map(withFrame: CGRect.zero, camera: camera)
        mapView.settings.zoomGestures = true
        view = mapView
        self.createMapViewMarker(mapView: mapView)
    }
    
    // create marker to display pindrop over map
    func createMapViewMarker(mapView: GMSMapView) {
        for (index, element) in names.enumerated() {
            let marker = GMSMarker()
            marker.position = CLLocationCoordinate2D(latitude: latitude[index], longitude: longitude[index])
                   marker.map = mapView
                   marker.title = element
                   let img = UIImage.init(named: "avatar" + String(index+1))
                   let markerView = UIImageView(image: img)
                   markerView.tintColor = UIColor.red
                   marker.iconView = markerView
                   
        }
    }
    
    // API to get location details
      func callgetDeviceLocationDetails() {
        let deviceURL = URL(string:  Constants.ApiPath.deviceApisUrl + "5e789ad0a789b5a7f632ff7e" + "?tsp=1585031229387&ugs_token=" + self.ugsToken)!
        DeviceService.shared.getDeviceLocationDetails(with: deviceURL) { (result : (Result<DeviceModel, Error>)) in
              switch result {
                      case .success(let deviceResponse):
                          print(deviceResponse.devicedata!)
                      case .failure(let error):
                          if type(of: error) == NetworkManager.ErrorType.self {
                              
                          } else {
                              DispatchQueue.main.async {
                              self.ShowALert(title: error.localizedDescription)
                              }
                      }
                  }
              }
      }
}