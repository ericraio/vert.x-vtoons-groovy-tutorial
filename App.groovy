def webServerConf = [

  // Normal web server stuff

  port: 8080,
  host: 'localhost',
  ssl: true,
  bridge: true,

  permitted: [
    // allow calls to login
    [
      address: 'vertx.basicauthmanager.login'
    ],
    // Allow calls to get static album data from the persistor
    [
      address: 'vertx.mongopersistor',
      match: [
        action: 'find',
        collection: 'albums'
      ]
    ],
    // And to place orders
    [
      address: 'vertx.mongopersistor',
      requires_auth: true, // User must be logged in to send let these through
      match: [
        action: 'save',
        collection: 'orders'
      ]
    ]

  ]
]

container.with {
  // Deploy a MongoDB persistor module
  deployVerticle('mongo-persistor') {
    deployVerticle('StaticData.groovy')
  }

  // Deploy an auth manager to handle the authentication
  deployVerticle('auth-mgr')

  // Start the web server, with the config we defined above
  container.deployVerticle('web-server', webServerConf);
}
