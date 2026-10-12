#!/usr/bin/env python3
# Copyright 2026 lamplis
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

"""Convert one ONNX file into a float32 TFLite model and a Core ML bundle."""

import argparse
import subprocess
import sys
import tempfile
from pathlib import Path


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(add_help=True)
    parser.add_argument("--onnx")
    parser.add_argument("--out")
    args = parser.parse_args(argv)
    if not args.onnx:
        print("Pass --onnx path/to/model.onnx", file=sys.stderr)
        return 1
    if not args.out:
        print("Pass --out path/to/output-directory", file=sys.stderr)
        return 1

    onnx_path = Path(args.onnx)
    if not onnx_path.is_file():
        print(f"ONNX file not found: {onnx_path}", file=sys.stderr)
        return 1

    stem = onnx_path.stem
    out_dir = Path(args.out)
    android_dir = out_dir / "android"
    ios_dir = out_dir / "ios"
    android_dir.mkdir(parents=True, exist_ok=True)
    ios_dir.mkdir(parents=True, exist_ok=True)

    tflite_path = android_dir / f"{stem}.tflite"
    write_tflite(onnx_path, tflite_path)
    write_coreml(onnx_path, ios_dir, stem)
    print(f"Wrote {tflite_path}")
    print(f"Wrote {ios_dir / f'{stem}.mlmodelc'}")
    return 0


def write_tflite(onnx_path: Path, tflite_path: Path) -> None:
    import onnx2tf
    import tensorflow as tf

    with tempfile.TemporaryDirectory(prefix="vision-onnx-") as temp:
        saved_dir = Path(temp) / "saved_model"
        onnx2tf.convert(
            input_onnx_file_path=str(onnx_path),
            output_folder_path=str(saved_dir),
            output_signaturedefs=True,
            non_verbose=True,
        )
        model_dir = find_saved_model(saved_dir)
        converter = tf.lite.TFLiteConverter.from_saved_model(str(model_dir))
        tflite_path.write_bytes(converter.convert())


def find_saved_model(folder: Path) -> Path:
    if (folder / "saved_model.pb").is_file():
        return folder
    for child in folder.iterdir():
        if child.is_dir() and (child / "saved_model.pb").is_file():
            return child
    raise SystemExit("onnx2tf did not write a SavedModel")


def write_coreml(onnx_path: Path, ios_dir: Path, stem: str) -> None:
    import coremltools as ct

    mlmodel = ct.convert(str(onnx_path), source="onnx", convert_to="mlprogram")
    mlpackage = ios_dir / f"{stem}.mlpackage"
    mlmodel.save(str(mlpackage))
    subprocess.run(
        ["xcrun", "coremlcompiler", "compile", str(mlpackage), str(ios_dir)],
        check=True,
    )
    compiled = ios_dir / f"{stem}.mlmodelc"
    if not compiled.is_dir():
        raise SystemExit(f"coremlcompiler did not write {compiled}")


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
