import os
import os.path
from flask import Flask, jsonify, request, send_from_directory

APP_ROOT = os.path.dirname(os.path.abspath(__file__))

UPLOAD_FOLDER = os.path.join(APP_ROOT, 'static', 'img', 'uploads')
OUTPUT_FOLDER = os.path.join(APP_ROOT, 'static', 'img', 'results')

app = Flask(__name__)

app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER
app.config['OUTPUT_FOLDER'] = OUTPUT_FOLDER

uploads_dir = os.path.join(app.instance_path, 'files')


@app.route('/', methods=['GET', 'POST'])
def method():
    if request.method == 'POST':
        file = request.files['image']
        filename = file.filename
        full_filename = os.path.join(app.config['UPLOAD_FOLDER'], filename)
        file.save(full_filename)

        img_url = send_from_directory(app.config['UPLOAD_FOLDER'], filename=filename, as_attachment=False)
        # print(filename)
        # file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
        # resultImg = facemask(file,filename)
        # resultImg = file
        # resultImgName ='filtered' + filename
        # full_filename = os.path.join(app.config['OUTPUT_FOLDER'], resultImgName)
        # resultImg.save()
        # data = {"success": "1", "url_image": "img_url"}
        data = {"success": "0", "url_image": "img_url"}
        return jsonify(data)
    else:
        #img_url = send_from_directory(app.config['OUTPUT_FOLDER'], filename='androidFlak.jpg', as_attachment=False)
        data = {"success": "1", "url_image": "img_url"}
        return jsonify(data)


if __name__ == '__main__':
    app.run()
