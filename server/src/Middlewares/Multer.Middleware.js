import multer from "multer"

const storage = multer.diskStorage({
    destination:function(req,file,cb){
        cb(null,"./public");
    },
    filename:function(req,file,cb){
        const date = Date.now();
        cb(null,file.originalname+date);
    }
})

const upload = multer({ storage });

export default upload;