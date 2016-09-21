# Simple module registration build on top of Guice

-Using META-INF/registry-services.guice as main entry point to load all services.
-Using META-INF/registry-services.guice.1 to override some of services in test environment
-Using META-INF/registry/layer.services to declare reusable services

Each services registry can include other services, register module or binding (and named) to implementation as well as provider