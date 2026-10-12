# KMP-Vision

**Build your vision experience once. Bring it to Android and iOS.**

Camera previews, on-device OCR, image picking, visual overlays, and model inference for Kotlin and Compose Multiplatform. Share your application logic and Compose UI, with native implementations underneath.

[Documentation](https://lamplis.github.io/kmp-vision/) · [Run the tutorial](tutorial/README.md) · [Installation](https://lamplis.github.io/kmp-vision/install/)

![Android and iPhone object-recognition concept](docs/images/phones-vision-concept.webp)

AI-generated concept illustration. Object detection requires your own compatible model and integration. For real native OCR screenshots and a working example, [run Vision Lab](tutorial/README.md).

## Choose your building blocks

- **Camera:** Compose preview, still capture, camera controls, and analysis frames.
- **OCR:** encoded image bytes to recognized text and normalized bounding boxes.
- **Overlays:** styleable rectangles and labels over your content.
- **Picker:** system image selection with resizing and filters.
- **Inference:** a shared API for compatible LiteRT and Core ML models.

Platform permissions, application setup, and inference model assets still need platform-specific configuration. See the documentation for supported behavior and integration requirements.

## Thanks

kmp-vision is a fork of [Peekaboo](https://github.com/onseok/peekaboo) by onseok. Peekaboo is no longer maintained, so this project continues that camera and image-picker work. Thanks to onseok and TEAM-PREAT for the original library.

The OCR approach also draws inspiration from Amirhossein Ijadi’s Compose Multiplatform Text Recognition. Maintained by [lamplis](https://github.com/lamplis). See [project origins and contribution guidance](https://lamplis.github.io/kmp-vision/contributing/).

## License

```
Copyright 2024 onseok

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
