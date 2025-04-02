import {v2 as cloudinary}  from "cloudinary"
import fs from "fs"

cloudinary.config({
    cloud_name:'dbltzkwv5', 
    api_key:  '623127711189112', 
    api_secret: 'vYCIVv1qrpWT7o-Ntyg8FIG3bkk'   
})

const UploadOnCloudinary = async (localFilePaths) => {
    try {
        if (!localFilePaths || (Array.isArray(localFilePaths) && localFilePaths.length === 0)) {
            console.log("Required file path for uploading");
            return [];
        }

        const filePaths = Array.isArray(localFilePaths) ? localFilePaths : [localFilePaths];

        // Upload files to Cloudinary
        const uploadPromises = filePaths.map(async (filePath) => {
            try {
                const result = await cloudinary.uploader.upload(filePath, { resource_type: "auto" });
                fs.unlinkSync(filePath); // Remove the local file after successful upload
                return result.secure_url;
            } catch (uploadError) {
                console.error(`Error uploading file ${filePath}:`, uploadError);
                return null; // Return null or some error indicator for this file
            }
        });

        const uploadUrls = await Promise.all(uploadPromises);

        // Filter out any null values (failed uploads)
        return uploadUrls.filter(url => url !== null);
    } catch (error) {
        console.error("Cloudinary Error:", error);
        throw error; // Rethrow error for calling function to handle
    }
};

export default UploadOnCloudinary;