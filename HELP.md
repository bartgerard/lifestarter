## JSON issue with @RequiredArgsConstructor

Immutability is a best practice, but to combine it with jackson metadata (@ConfigurationProperties) is required.
In the latest version of lombok, this is no longer automatic and a lombok.config file is required with the following line:

    lombok.anyConstructor.addConstructorProperties=true

Place the file somewhere in your project (root?) and also at the following line to prevent lombok from searching for additional config files:

    config.stopBubbling = true

https://projectlombok.org/features/configuration

https://github.com/rzwitserloot/lombok/issues/1563

https://github.com/openanthem/nimbus-core/pull/282

Guest class was able to be constructed from json, other class weren't.