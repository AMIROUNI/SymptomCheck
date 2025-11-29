from django.db import models

class AiRequestLog(models.Model):
    patient_id = models.PositiveIntegerField(null=True, blank=True)
    image_name = models.CharField(max_length=255, null=True, blank=True)
    predicted_label = models.CharField(max_length=100)
    confidence = models.FloatField()
    requested_at = models.DateTimeField(auto_now_add=True)
    model_version = models.CharField(max_length=50, default="v1")

    def __str__(self):
        return f"{self.predicted_label} ({self.confidence:.2f})"
