package pet.loyal.provider.view.editpetcard

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import pet.loyal.provider.databinding.LayoutPhotoGalleryItemBinding

class PhaseMessageGalleryRecyclerViewAdapter(
    private val messageId: String,
    private val position: Int,
    private val imageList: ArrayList<Uri>?,
    private val imageItemListener: ImageItemListener
) :
    RecyclerView.Adapter<PhaseMessageGalleryRecyclerViewAdapter.GalleryViewHolder>() {

    interface ImageItemListener {
        fun onClickImage(positionImage: Int, position: Int, messageId: String)
        fun onClickDelete(positionImage: Int, position: Int, messageId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {

        val layoutPhotoGalleryItemBinding = LayoutPhotoGalleryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return GalleryViewHolder(layoutPhotoGalleryItemBinding)
    }

    override fun getItemCount(): Int {
        return imageList?.size ?: 0
    }

    override fun onBindViewHolder(viewHolder: GalleryViewHolder, position: Int) {

        var image = imageList?.get(position)
        if (image != null) {
            val imageView = viewHolder.itemBinding
            Picasso.get().load(image).into(imageView.imgSource)

            imageView.imgSource.setOnClickListener {
                imageItemListener.onClickImage(position, this.position, messageId)
            }

            imageView.btnDelete.setOnClickListener {
                imageItemListener.onClickDelete(position, this.position, messageId)
            }
        }
    }

    inner class GalleryViewHolder(val itemBinding: LayoutPhotoGalleryItemBinding):
        RecyclerView.ViewHolder(itemBinding.root)
}