//
//  RegistrationScreen.swift
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

class RegistrationScreen: BaseViewController,UITextFieldDelegate {
    
    @IBOutlet weak var userNameTxt: UITextField!
    @IBOutlet weak var mobileNumberTxt: UITextField!
    @IBOutlet weak var otpTxt: UITextField!
    @IBOutlet weak var resendOtpBtn: UIButton!
    @IBOutlet weak var scrollView: UIScrollView!
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "Registration"
        self.navigationItem.setHidesBackButton(true, animated: true)
        self.initilaiseData()
    }
    
    func initilaiseData(){
        self.userNameTxt.delegate = self
        self.mobileNumberTxt.delegate = self
        self.otpTxt.delegate = self
        
        userNameTxt.inputAccessoryView = self.setToolbarWithDoneButton()
        mobileNumberTxt.inputAccessoryView = self.setToolbarWithDoneButton()
        otpTxt.inputAccessoryView = self.setToolbarWithDoneButton()
        
        self.createBackBarButtonItem()
    }
    
    func setToolbarWithDoneButton() -> UIToolbar{
           let flexSpace = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
           let keyboardDoneButtonView = UIToolbar.init()
           keyboardDoneButtonView.sizeToFit()
           let doneButton = UIBarButtonItem.init(barButtonSystemItem: UIBarButtonItem.SystemItem.done,
                                                                     target: self,
                                                                     action: #selector(doneClicked(sender:)))

           keyboardDoneButtonView.items = [flexSpace,doneButton]
           return keyboardDoneButtonView
       }
      
       @objc func doneClicked(sender: AnyObject) {
         self.view.endEditing(true)
       }
    
    func createBackBarButtonItem() {
          let backBtn : UIBarButtonItem = UIBarButtonItem.init(image: UIImage(named: "back"), style: .plain, target: self, action: #selector(backButton(sender:)))
          backBtn.tintColor = .white
          self.navigationItem.setLeftBarButton(backBtn, animated: true)
      }
      
      @objc func backButton(sender: UIBarButtonItem) {
          self.navigationController?.popViewController(animated: true)
      }
    
    @IBAction func registerBtnAction(_ sender: Any) {
        
        // validation check
        if userNameTxt.text?.count == 0{
            self.ShowALert(title: Constants.LoginScreenConstants.UserName)
            return
        }
        
        if mobileNumberTxt.text?.count == 0 || !(mobileNumberTxt.text?.isValidPhone ?? true){
            self.ShowALert(title: Constants.LoginScreenConstants.PhoneNumber);
            return
        }
        
        if otpTxt.text?.count == 0{
            self.ShowALert(title: Constants.LoginScreenConstants.Otp);
            return
        }
        
        self.registerUserApi()
    }
    
    
    @IBAction func resendOtpBtnAction(_ sender: Any) {
        if mobileNumberTxt.text?.count == 0 || !(mobileNumberTxt.text?.isValidPhone ?? true){
            self.ShowALert(title: Constants.LoginScreenConstants.PhoneNumber);
            return
        }
        self.generateTokenApiCall(tokenUrl: Constants.ApiPath.GenerateRegistrationTokenUrl, params: ["type": "registration","phone": self.mobileNumberTxt.text!,"phoneCountryCode": "91"])
        self.resendOtpBtn.setTitle("Resend OTP", for: .normal)
    }
    
    // MARK: Service Calls
    
    //  Register user
    func registerUserApi() {
        let registerURL = URL(string: Constants.ApiPath.RegisterationUrl)!
        let userDetails : [String : Any] = ["token": ["value" : self.otpTxt.text!],"phone": self.mobileNumberTxt.text!,"phoneCountryCode": "91","name": self.userNameTxt.text!,"password":"Borqs@1234"]
        UserService.shared.registerUser(registerTokenUrl: registerURL, parameters: userDetails) { (result :Result<UserModel, Error>) in
            switch result {
            case .success( _):
                DispatchQueue.main.async {
                    self.hideActivityIndicator()
                    self.navigateToLoginScreen()
                }
            case .failure(let error):
                if type(of: error) == NetworkManager.ErrorType.self {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        self.ShowALert(title: Utils.shared.handleError(error: error as! NetworkManager.ErrorType))
                    }
                } else {
                    DispatchQueue.main.async {
                        self.hideActivityIndicator()
                        self.ShowALert(title: error.localizedDescription)
                    }
                }
            }
        }
    }
    
    // navigate to login screen
    func navigateToLoginScreen() {
        let storyBoard : UIStoryboard = UIStoryboard(name: "Main", bundle:nil)
        let nextViewController = storyBoard.instantiateViewController(withIdentifier: Constants.ScreenNames.LoginScreen) as! LoginScreen
        self.navigationController?.pushViewController(nextViewController, animated: true)
    }
    
    // MARK: UITextField Delegate
       
       func textFieldDidBeginEditing(_ textField: UITextField) {
           scrollView.setContentOffset(CGPoint(x: 0, y: (textField.superview?.frame.origin.y)! + 50), animated: true)
       }

       func textFieldDidEndEditing(_ textField: UITextField) {
           scrollView.setContentOffset(CGPoint(x: 0, y: 0), animated: true)
       }
    
}
