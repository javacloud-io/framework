## Simple module registration build on top of Guice

Each services registry can include other services, register module or binding (and named) to implementation as well as provider

1. Using META-INF/javacloud.cdi.services as main entry point to load all services.
2. Using META-INF/layer.services to declare reusable services
