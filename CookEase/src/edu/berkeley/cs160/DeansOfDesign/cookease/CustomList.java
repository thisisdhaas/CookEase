package edu.berkeley.cs160.DeansOfDesign.cookease;

public class CustomList {
        private String name;
        private String alertType;
        
        // Constructor for the Phonebook class
        public CustomList(String name, String alertType) {
                super();
                this.name = name;
                this.alertType = alertType;
        }
        
        // Getter and setter methods for all the fields.
        // Though you would not be using the setters for this example,
        // it might be useful later.
        public String getName() {
                return name;
        }
        public void setName(String name) {
                this.name = name;
        }
        public String getPhone() {
                return alertType;
        }
        public void setPhone(String phone) {
                this.alertType = phone;
        }
}
