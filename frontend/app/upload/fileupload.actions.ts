"use server";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://springboot-backend:8080/api"


export async function fileUpload(files: File[]) {

  if (!files) {
    return { success: false, message: "No files to upload" };
  }

  try {
    const formData = new FormData();
    files.forEach((file) => formData.append("files", file));

    const response = await fetch(`${API_BASE_URL}/dicom/upload`, {
      method: 'POST',
      body: formData
    });

    const data = await response.json();

    if (!response.ok) {
      return { success: false, message: `Error uploading files: ${JSON.stringify(data)}` };
    }


    return { success: true, message: JSON.stringify(data.message) };



  } catch (error) {
    console.log(error);
    return { success: false, error: `Error while uploading files: ${error}` };
  }
}
