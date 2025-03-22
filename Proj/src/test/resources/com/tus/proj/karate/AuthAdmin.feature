Feature: Authenticate and get admin JWT token

Scenario: Get admin JWT Token
    Given url 'http://localhost:9092'
    And path '/api/users/login'
    And request { username: 'admin', password: 'admin' }  # Use valid credentials
    When method post
    Then status 200
    And match response.jwt == '#notnull'
    
    * def token = response.jwt
