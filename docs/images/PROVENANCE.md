# Preview provenance

The documentation combines real tutorial captures and explicitly labelled AI-generated concept illustrations.

## AI-generated product concepts

`phones-vision-concept.webp` and `object-recognition-concept.webp` were generated with the built-in image-generation tool on 2026-10-12. Phone bodies, scenes, app interfaces, detection boxes, labels, and scene descriptions are invented. The iPhone artwork deliberately includes a Dynamic Island, while the Android artwork uses a centered punch-hole camera. These assets promote possible product experiences; they are not evidence of runtime performance, bundled object detection, or LLM/VLM support. They were encoded as WebP without compositional changes. See [the exact generation prompts](GENERATED-PROMPTS.md).

## Real tutorial captures

The tutorial previews were captured from the `tutorial/` project in this repository on 2026-10-12.

- **Android:** Pixel 9 Pro emulator, Android API 36. The tutorial is compiled against API 37 and runs native ML Kit text recognition.
- **iOS:** iPhone 17 Pro simulator, iOS 26.2. The same shared tutorial runs Apple Vision text recognition.
- **Input:** `tutorial/common/src/commonMain/composeResources/files/vision-card.png`, a synthetic text card created for this tutorial. It contains no personal or third-party image content.
- **Camera:** actual tutorial camera preview displaying the Android emulator's virtual scene, not a physical-camera recording.
- **Picker:** actual Android system picker opened by the tutorial, containing the bundled sample card imported into the emulator.
- **Processing:** `tools/build_doc_previews.py` resizes captures and places them on a presentation canvas. The OCR and picker GIFs sequence captured states with edited timing; they are not performance measurements. No recognition results are drawn into or substituted in a screenshot.
- **Diagrams:** `architecture.svg` and `model-pipeline.svg` are explanatory illustrations, not runtime captures. The tutorial does not currently include a compatible inference model, so the models page pairs labelled concept art with the integration diagram; it does not claim a runtime inference demonstration.

To reproduce captures, follow `tutorial/README.md` and run the asset tool with Pillow and a local sans-serif font. Raw captures are kept outside the source tree; the optimized previews and their generator are versioned here.
