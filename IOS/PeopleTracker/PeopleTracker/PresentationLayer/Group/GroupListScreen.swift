//
//  GroupListScreen.swift
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
import JJFloatingActionButton

class GroupListScreen: UIViewController,UITableViewDelegate, UITableViewDataSource {
    
     var navtitle : String = ""
     var groupId  : String = ""
     @IBOutlet weak var groupListTableView: UITableView!
     @IBOutlet weak var instructionLbl: UILabel!
    
       let actionButton = JJFloatingActionButton()
       var groupMemberData: [GroupMemberData] = []
       var dbMemberData: [GroupMemberData] = []
       
       override func viewDidLoad() {
           super.viewDidLoad()
           self.title = navtitle
           groupListTableView.delegate = self
           groupListTableView.dataSource = self
           let trackBtn : UIBarButtonItem = UIBarButtonItem.init(title: "Track", style: .plain, target: self, action: #selector(trackButton(sender:)))
           trackBtn.tintColor = UIColor.white
           self.navigationItem.setRightBarButton(trackBtn, animated: true)
           floatingActionButton()
           groupListTableView.tableFooterView = UIView()
           self.createNotification()
           self.getMemberInGroupApi()
       }
    
      func createNotification() {
           NotificationCenter.default.addObserver(self, selector: #selector(callgetMembersIngroupApi(notification:)), name: NSNotification.Name(rawValue: Constants.NotificationName.GetMemebersInGroup), object: nil)
       }
       
       @objc func callgetMembersIngroupApi(notification: NSNotification) {
           self.getMemberInGroupApi()
       }
       
    func showHideTableView() {
        
        if self.groupMemberData.count > 0 {
                          self.instructionLbl.isHidden = true
                          self.groupListTableView.isHidden = false
                      } else {
                          self.instructionLbl.isHidden = false
                          self.groupListTableView.isHidden = true
                      }
    }
       
       // MARK: UITableView Delegate and DataSource methods
       
       func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
           return self.groupMemberData.count
       }
       
       func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
           return 135.0
       }
       
       func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
           
           let cell = tableView.dequeueReusableCell(withIdentifier: "cellIdentifier") as! GroupCell
           cell.selectionStyle = .none
           let memberData = groupMemberData[indexPath.row]
           cell.setUserData(memberData: memberData)
           return cell
       }
       
       // MARK: Navigation methods
       func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
           
       }
    
    
        @objc func trackButton(sender: UIBarButtonItem) {
            //navigateToMapsScreen()
        }
       
       func navigateToMapsScreen() {
           let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let mapsViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.MapsScreen) as! MapsScreen
           self.navigationController?.pushViewController(mapsViewController, animated: true)
       }
       
       func navigateToAddDeviceScreen(title : String) {
             let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let addDeviceViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.AddDeviceScreen) as! AddDeviceScreen
             addDeviceViewController.navtitle = title
             self.navigationController?.pushViewController(addDeviceViewController, animated: true)
         }
       func navigateToAddPersonScreen(title : String) {
              let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
              let addPersonViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.AddPersonScreen) as! AddPersonScreen
              addPersonViewController.navtitle = title
              addPersonViewController.groupId = self.groupId
              self.navigationController?.pushViewController(addPersonViewController, animated: true)
          }
    
       func navigateToCreateGroupScreen() {
              let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let createGroupViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.CreateGroupScreen) as! CreateGroupScreen
              self.navigationController?.pushViewController(createGroupViewController, animated: true)
          }
       
       // MARK: Show floating button
       func floatingActionButton () {
        actionButton.addItem(title: Constants.HomScreenConstants.AddDevice, image: UIImage(named: "device")?.withRenderingMode(.alwaysTemplate)) { item in
               // do something
            self.navigateToAddDeviceScreen(title: Constants.HomScreenConstants.AddDevice)
           }
           
        actionButton.addItem(title: Constants.HomScreenConstants.AddPerson, image: UIImage(named: "user4")?.withRenderingMode(.alwaysTemplate)) { item in
               // do something
            self.navigateToAddPersonScreen(title: Constants.HomScreenConstants.AddPerson)
           }
         
           view.addSubview(actionButton)
           actionButton.translatesAutoresizingMaskIntoConstraints = false
           actionButton.trailingAnchor.constraint(equalTo: view.safeAreaLayoutGuide.trailingAnchor, constant: -16).isActive = true
           actionButton.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -16).isActive = true
       }
    
       
    // Get memebers in group Api Call
    func getMemberInGroupApi() {
        self.showActivityIndicator()
        let getListOfMembersUrl : URL = URL(string: Constants.ApiPath.UserApisUrl + Utils.shared.getUserId() + Constants.ApiPath.CreateGroupUrl + "/" + self.groupId + Constants.ApiPath.CreateMultiple )!
        GroupService.shared.getMembersFromGroup(getMembersInGroupUrl:  getListOfMembersUrl) { (result : Result<GroupMemberModel, Error>) in
            switch result {
            case .success(let groupResponse):
               self.groupMemberData.removeAll()
//               let memberData = RealmManager.sharedInstance.getGroupMemeberDataFromDB()
               for response in groupResponse.groupMemberData {
                if response.status != "removed" {
//                    for member in memberData {
//                        let memberArr = member.groupMemberData.filter { $0.groupMemberId == response.groupMemberId }
//                        response.name = memberArr.first?.name
//                        print(memberArr)
//                    }
                    self.groupMemberData.append(response)
                }
                }
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.showHideTableView()
                    self.groupListTableView.reloadData()
                }
            case .failure(let error):
                if type(of: error) == NetworkManager.ErrorType.self {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        if error as! NetworkManager.ErrorType == NetworkManager.ErrorType.ExceededGroupLimit{
                            self.ShowALert(title: Constants.ErrorMessage.MobileNumberExists)
                        } else {
                          self.ShowALert(title: Utils.shared.handleError(error: error as! NetworkManager.ErrorType))
                        }
                    }
                } else {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        self.ShowALert(title: error.localizedDescription)
                    }
                }
                self.showHideTableView()
            }
        }
    }
    
}
