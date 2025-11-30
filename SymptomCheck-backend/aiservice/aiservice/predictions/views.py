from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from io import BytesIO
from keras.utils import load_img, img_to_array
import numpy as np

from .serializers import ImageUploadSerializer
from .models import AiRequestLog
from .ai_model.model_loader import predict_image


# List of class names matching your model's output indices
classes = [
    'Acne and Rosacea Photos',
    'Actinic Keratosis Basal Cell Carcinoma and other Malignant Lesions',
    'Atopic Dermatitis Photos',
    'Cellulitis Impetigo and other Bacterial Infections',
    'Eczema Photos',
    'Exanthems and Drug Eruptions',
    'Herpes HPV and other STDs Photos',
    'Light Diseases and Disorders of Pigmentation',
    'Lupus and other Connective Tissue diseases',
    'Melanoma Skin Cancer Nevi and Moles',
    'Poison Ivy Photos and other Contact Dermatitis',
    'Psoriasis pictures Lichen Planus and related diseases',
    'Seborrheic Keratoses and other Benign Tumors',
    'Systemic Disease',
    'Tinea Ringworm Candidiasis and other Fungal Infections',
    'Urticaria Hives',
    'Vascular Tumors',
    'Vasculitis Photos',
    'Warts Molluscum and other Viral Infections'
]


class PredictView(APIView):

    def post(self, request, format=None):
        serializer = ImageUploadSerializer(data=request.data)

        if serializer.is_valid():
            img_file = serializer.validated_data["image"]

            try:
                # Convert uploaded image to BytesIO
                img_bytes = img_file.read()
                img_io = BytesIO(img_bytes)
                
                # Load & preprocess image to match model input
                target_size = (192, 192)  # model expects square input
                img = load_img(img_io, target_size=target_size)
                img_array = img_to_array(img) / 255.0  # normalize to [0,1]

                # Predict using your model
                class_index, confidence = predict_image(img_array)

                # Map index to class name
                class_name = classes[class_index]

                # Save request log
                AiRequestLog.objects.create(
                    patient_id=None,
                    image_name=img_file.name,
                    predicted_label=class_name,
                    confidence=confidence,
                    model_version="v1"
                )

                return Response({
                    "class_name": class_name,
                    "confidence": float(confidence)
                }, status=status.HTTP_200_OK)

            except Exception as e:
                return Response({
                    "error": f"Error processing image: {str(e)}"
                }, status=status.HTTP_400_BAD_REQUEST)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)
