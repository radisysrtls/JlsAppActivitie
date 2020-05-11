//
//  ConsentService.swift
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

class ConsentService {
    public static let shared = ConsentService()
    private init() {}
    
    let accessKey = "bearer " + Utils.shared.getUgsToken()
    
    
    // Api to generate consent token
    func generateConsentToken(generateConsentUrl: URL,parameters: [String : Any], completion: @escaping (Result<ConsentModel, Error>) -> Void) -> Void  {
        
        let networkManager = NetworkManager.init(url: generateConsentUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.post, accessKey: accessKey,parameters: parameters)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let groupMemberData = try! JSONDecoder().decode(ConsentModel.self, from: jsonData)
                                completion(.success(groupMemberData))
                            }
                        }
                    }
                }
                catch {
                    let msg = error.localizedDescription
                    completion(.failure(error))
                    print(msg)
                }
            case .failure(let error):
                completion(.failure(error))
                print(error)
            }
        }
        
    }
    
    // Api to approve consent token
    func approveConsentToken(approveConsentUrl: URL,parameters: [String : Any], completion: @escaping (Result<ConsentModel, Error>) -> Void) -> Void  {
        
        let networkManager = NetworkManager.init(url: approveConsentUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.post, accessKey: accessKey,parameters: parameters)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let groupMemberData = try! JSONDecoder().decode(ConsentModel.self, from: jsonData)
                                completion(.success(groupMemberData))
                            }
                        }
                    }
                }
                catch {
                    let msg = error.localizedDescription
                    completion(.failure(error))
                    print(msg)
                }
            case .failure(let error):
                completion(.failure(error))
                print(error)
            }
        }
        
    }
    
    // Api to reject consent token
    func rejectConsentToken(rejectConsentUrl: URL,parameters: [String : Any], completion: @escaping (Result<ConsentModel, Error>) -> Void) -> Void  {
        
        let networkManager = NetworkManager.init(url: rejectConsentUrl)
        let request = networkManager.buildRequest(method: NetworkManager.Method.post, accessKey: accessKey,parameters: parameters)
        networkManager.sendRequest(request: request) { (result) in
            switch result {
            case .success(_, let data) :
                do {
                    if let resultdata = data {
                        if let json = try JSONSerialization.jsonObject(with: resultdata) as? [String : Any] {
                            let data = try JSONSerialization.data(withJSONObject: json, options: [])
                            if let string = String(data: data, encoding: String.Encoding.utf8) {
                                let jsonData = string.data(using: .utf8)!
                                let groupMemberData = try! JSONDecoder().decode(ConsentModel.self, from: jsonData)
                                completion(.success(groupMemberData))
                            }
                        }
                    }
                }
                catch {
                    let msg = error.localizedDescription
                    completion(.failure(error))
                    print(msg)
                }
            case .failure(let error):
                completion(.failure(error))
                print(error)
            }
        }
        
    }
    
}