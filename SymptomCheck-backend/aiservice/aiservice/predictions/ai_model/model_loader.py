import os
from django.conf import settings
from keras.models import load_model
import numpy as np

def predict_image(img_array):
    try:
        # Get the base directory of your Django project
        base_dir = settings.BASE_DIR
        
        # Construct the model path relative to your project
        model_path = os.path.join(
            base_dir, 
            'predictions', 
            'ai_model', 
            'model_files', 
            'my_model.h5'
        )
        
        # Alternative: if model is in the same directory as model_loader.py
        # current_dir = os.path.dirname(os.path.abspath(__file__))
        # model_path = os.path.join(current_dir, 'model_files', 'my_model.h5')
        
        print(f"Looking for model at: {model_path}")  # Debug print
        print(f"File exists: {os.path.exists(model_path)}")  # Debug print
        
        if not os.path.exists(model_path):
            raise FileNotFoundError(f"Model file not found at: {model_path}")
        
        # Load the model
        model = load_model(model_path)
        
        # Make prediction
        img_array = np.expand_dims(img_array, axis=0)
        prediction = model.predict(img_array)
        class_index = np.argmax(prediction[0])
        confidence = float(prediction[0][class_index])
        
        return class_index, confidence
        
    except Exception as e:
        print(f"Error in predict_image: {str(e)}")
        raise