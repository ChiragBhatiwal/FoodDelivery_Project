import mongoose from "mongoose"

const LocationSchema = mongoose.Schema(
  {
    userId: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User"
    },
    location: {
      type: { type: String, enum: ["Point"], required: true },
      coordinates: { type: [Number], required: true },
    },
    Address: {
      type: String,
      required: true
    }

  }, { timesstamps: true })

LocationSchema.index({ location: "2dsphere" });

export default mongoose.model("Location", LocationSchema)    