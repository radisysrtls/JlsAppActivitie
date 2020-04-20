//
//  LoginModel.swift
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


import Foundation

public struct LoginModel : Codable {
    
    let ugsToken        : String
    let ugsTokenexpiry  : Double
    let user            : User?
    
    private enum CodingKeys : String, CodingKey {
        case ugsToken = "ugs_token", ugsTokenexpiry = "ugs_token_expiry",user
    }
    
}

struct User : Codable {
    
    let userId        : String
    let email         : String?
    let usertype      : String
    let name          : String
    let phone         : String
    let wearableUsers : [WearableUsers]?
    
    private enum CodingKeys : String, CodingKey {
        case userId = "_id", email, usertype = "type", name, phone,wearableUsers
    }
}
struct WearableUsers : Codable {
    
    let wearableDeviceId : String
    let deviceId         : String
    
    private enum CodingKeys: String, CodingKey {
        case wearableDeviceId = "_id", deviceId = "deviceId"
    }
    
}
