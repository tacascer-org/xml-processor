# Changelog

## [0.7.4](https://github.com/tacascer-org/xml-processor/compare/v0.7.3...v0.7.4) (2024-05-25)


### Bug Fixes

* **api:** don't expose `toDocument` function ([#69](https://github.com/tacascer-org/xml-processor/issues/69)) ([6721d05](https://github.com/tacascer-org/xml-processor/commit/6721d0581c79d48a2ed289c8ad2a6f32bc0908b1))

## [0.7.3](https://github.com/tacascer-org/xml-processor/compare/v0.7.2...v0.7.3) (2024-05-12)


### Bug Fixes

* Create parent directories if file doesn't exist ([#63](https://github.com/tacascer-org/xml-processor/issues/63)) ([8010fec](https://github.com/tacascer-org/xml-processor/commit/8010fec798769f3224204571bbef7bf447c8e48b))
* **XmlFilterChain:** pretty format output of process(Path, Path) ([#65](https://github.com/tacascer-org/xml-processor/issues/65)) ([181f2cf](https://github.com/tacascer-org/xml-processor/commit/181f2cfb40537ac178ec94ab8cd8a9af6fc6be8d))

## [0.7.2](https://github.com/tacascer-org/xml-processor/compare/v0.7.1...v0.7.2) (2024-05-12)


### Bug Fixes

* **XmlFilterChain:** create new file if it doesn't exist ([#60](https://github.com/tacascer-org/xml-processor/issues/60)) ([238eca4](https://github.com/tacascer-org/xml-processor/commit/238eca49f6ed441c12c9e15926bd97306503d9d9))

## [0.7.1](https://github.com/tacascer-org/xml-processor/compare/v0.7.0...v0.7.1) (2024-05-09)


### Bug Fixes

* create new file if it doesn't exist ([#58](https://github.com/tacascer-org/xml-processor/issues/58)) ([3d11db2](https://github.com/tacascer-org/xml-processor/commit/3d11db2f892a6da1dc17be12b0912a322e301d2e))

## [0.7.0](https://github.com/tacascer-org/xml-processor/compare/v0.6.0...v0.7.0) (2024-05-08)


### Features

* Introduce `ImportFlattener` class for flattening XML imports ([#55](https://github.com/tacascer-org/xml-processor/issues/55)) ([289fafd](https://github.com/tacascer-org/xml-processor/commit/289fafdca5fbd1c0bb1dd49557a90d999ea1dd1a))

## [0.6.0](https://github.com/tacascer-org/xml-processor/compare/v0.5.0...v0.6.0) (2024-04-27)


### Features

* follow API guidelines ([#46](https://github.com/tacascer-org/xml-processor/issues/46)) ([3d20150](https://github.com/tacascer-org/xml-processor/commit/3d201507411247f4fed3652078421c9bf6a0adeb))
* NamespaceRemover ignore `xs:` ([#50](https://github.com/tacascer-org/xml-processor/issues/50)) ([f6df429](https://github.com/tacascer-org/xml-processor/commit/f6df4295aefc7f7ff85841661d87db51607bc4d3))

## [0.5.0](https://github.com/tacascer-org/xml-processor/compare/v0.4.0...v0.5.0) (2024-04-19)


### Features

* add filter chaining ([#44](https://github.com/tacascer-org/xml-processor/issues/44)) ([45e64fa](https://github.com/tacascer-org/xml-processor/commit/45e64fa4594774a4725dea4c1c9590ddf51c65bf))
* namespace remover ([#42](https://github.com/tacascer-org/xml-processor/issues/42)) ([3907330](https://github.com/tacascer-org/xml-processor/commit/3907330626fbb7de1d8688d427c615c307bb8bf9))

## [0.4.0](https://github.com/tacascer-org/xml-processor/compare/v0.3.0...v0.4.0) (2024-04-16)


### Features

* add classpath parsing ([#40](https://github.com/tacascer-org/xml-processor/issues/40)) ([3653001](https://github.com/tacascer-org/xml-processor/commit/3653001c1d97d31b0c9b4dc7f48125577404bdda))

## [0.3.0](https://github.com/tacascer-org/xml-processor/compare/v0.2.1...v0.3.0) (2024-04-15)


### Features

* can strip namespace ([#32](https://github.com/tacascer-org/xml-processor/issues/32)) ([872c674](https://github.com/tacascer-org/xml-processor/commit/872c674dcb14f8297de03d426cdb118b23ee12c1))

## [0.2.1](https://github.com/tacascer-org/xml-processor/compare/v0.2.0...v0.2.1) (2024-04-11)


### Bug Fixes

* remove jdom references from API ([#25](https://github.com/tacascer-org/xml-processor/issues/25)) ([8c1886c](https://github.com/tacascer-org/xml-processor/commit/8c1886c4c3c0fb88c222dee8621c99dbabe5474e))

## [0.2.0](https://github.com/tacascer-org/xml-processor/compare/v0.1.0...v0.2.0) (2024-04-11)


### Features

* output to given file ([#24](https://github.com/tacascer-org/xml-processor/issues/24)) ([6659539](https://github.com/tacascer-org/xml-processor/commit/66595391441fcfe27c8cb9f9fbde003ce2ae5705))

## [0.2.0-rc.1](https://github.com/tacascer-org/xml-processor/compare/v0.1.1-rc.1...v0.2.0-rc.1) (2024-04-10)


### Features

* Flatten xml with file URIs ([#6](https://github.com/tacascer-org/xml-processor/issues/6)) ([fb421f7](https://github.com/tacascer-org/xml-processor/commit/fb421f7b4ff683af40f81a6d146fcf3a10aec172))
* recursively flatten files ([#8](https://github.com/tacascer-org/xml-processor/issues/8)) ([efb961f](https://github.com/tacascer-org/xml-processor/commit/efb961f3cdc1d790f856a29710636372d2d6c397))

## [0.1.0](https://github.com/tacascer-org/xml-processor/compare/v0.0.1...v0.1.0) (2024-04-10)


### Features

* Flatten xml with file URIs ([#6](https://github.com/tacascer-org/xml-processor/issues/6)) ([fb421f7](https://github.com/tacascer-org/xml-processor/commit/fb421f7b4ff683af40f81a6d146fcf3a10aec172))
* recursively flatten files ([#8](https://github.com/tacascer-org/xml-processor/issues/8)) ([efb961f](https://github.com/tacascer-org/xml-processor/commit/efb961f3cdc1d790f856a29710636372d2d6c397))
