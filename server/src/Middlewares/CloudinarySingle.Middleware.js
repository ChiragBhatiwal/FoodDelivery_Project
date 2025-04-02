import { v2 as cloudinary } from "cloudinary"
import fs from "fs"

cloudinary.config({
    cloud_name: 'dbltzkwv5',
    api_key: '623127711189112',
    api_secret: 'vYCIVv1qrpWT7o-Ntyg8FIG3bkk'
})

const UploadOnCloudinarySingle = async (localFilePath) => {

    try {
        if (!localFilePath) {
            return console.log("LocalFilePath is Required");
        }

        const imageUrl = await cloudinary.uploader.upload(localFilePath, { resource_type: "auto" });

        fs.unlinkSync(localFilePath);
        return imageUrl;
    } catch (error) {
        console.log(error);
    }

}

export default UploadOnCloudinarySingle;